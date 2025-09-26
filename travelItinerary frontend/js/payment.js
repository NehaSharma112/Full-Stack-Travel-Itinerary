const PAYMENT_API = 'http://localhost:8080/api/payment/process';
class PaymentHandler{
    constructor(){
        this.selectedPaymentMethod='pm_card_visa';
        this.selectedPlan='PRO';
        this.init();
    }

    //calls init to attach listeneres
    init(){
        this.setEventListeners();
        this.initializeDefaults();
        this.updateSummary();
    }

    setEventListeners(){
        //selection of payment method and saved into variable
        document.querySelectorAll('.payment-method-card').forEach(card=>{ 
            card.addEventListener('click',(event)=>{
                this.selectPaymentMethod(event.currentTarget);
            })
        });

        //subsription selection
        document.querySelectorAll('.subscription-plan-card').forEach(card => {
            card.addEventListener('click', (e) => {
                this.selectSubscriptionPlan(e.currentTarget);
            });
        });

        document.getElementById('currency').addEventListener('change', () => {
            this.updatePricesForCurrency(); // This will update all 3 plan cards
            this.updateSummary(); // This will update the summary
        });
        //form submission
        document.getElementById('paymentForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.processPayment();
        });

        // Real-time validation
        this.setupValidation();
    }

    initializeDefaults() {
        // Set default payment method
        const defaultPaymentCard = document.querySelector('[data-method="pm_card_visa"]');
        if (defaultPaymentCard) {
            this.selectPaymentMethod(defaultPaymentCard);
        }

        // Set default subscription plan (PRO)
        const defaultPlanCard = document.querySelector('[data-plan="PRO"]');
        if (defaultPlanCard) {
            this.selectSubscriptionPlan(defaultPlanCard);
        }
    }

    selectPaymentMethod(selectedCard){

        //removing selection
        document.querySelectorAll('.payment-method-card').forEach(card => {
            card.classList.remove('selected'); //remove selected method
            const checkIcon = card.querySelector('.fa-check-circle');
            if (checkIcon) checkIcon.style.display = 'none';//unselecting all cards
        });

        //add selection only to selected card
        selectedCard.classList.add('selected');
        const checkIcon = selectedCard.querySelector('.fa-check-circle') || this.createCheckIcon(selectedCard);//if icon exist then A otherwise B
        checkIcon.style.display = 'inline';

        // Update selected method
        this.selectedPaymentMethod = selectedCard.dataset.method;
        document.getElementById('paymentMethodId').value = this.selectedPaymentMethod;
        
        this.updateSummary();
    }

    createCheckIcon(card) {
        const checkIcon = document.createElement('i');
        checkIcon.className = 'fas fa-check-circle text-success ms-auto';//made new class
        const container = card.querySelector('.d-flex');
        container.appendChild(checkIcon);
        return checkIcon;
    }


    selectSubscriptionPlan(selectedCard) {
        // Remove selection from all plan
        document.querySelectorAll('.subscription-plan-card').forEach(card => {
            card.classList.remove('selected');
        });

        // Add selection to selectedCard
        selectedCard.classList.add('selected');

        // Update selected plan
        this.selectedPlan = selectedCard.dataset.plan;
        document.getElementById('selectedPlan').value = this.selectedPlan;

        // Detect selected currency
        const currency = document.getElementById('currency').value.toLowerCase();

        // Get price based on dataset
        const priceAttr = `price${currency.charAt(0).toUpperCase() + currency.slice(1)}`;
        const planPrice = parseFloat(selectedCard.dataset[priceAttr]);
        document.getElementById('amountSymbol').textContent = this.getCurrencySymbol(currency);

        // Update amount based on selected plan
        // const planPrice = parseFloat(selectedCard.dataset.price);////////////////
        document.getElementById('amount').value = planPrice;// hidden input for backend
        document.getElementById('amountDisplay').value = this.formatCurrency(planPrice, currency); // visible for user
        
        this.updateSummary();
    }

    getCurrencySymbol(currency) {
        const symbols = {
            'usd': '$',
            'eur': '€',
            'gbp': '£',
            'cad': 'C$',
            'inr': '₹'
        };
        return symbols[currency.toLowerCase()] || '$';
    }


    // NEW METHOD: Update prices when currency changes (keeps current plan selection)
    updatePricesForCurrency() {
        const currency = document.getElementById('currency').value.toLowerCase();
        
        // Update currency symbol in amount input
        document.getElementById('amountSymbol').textContent = this.getCurrencySymbol(currency);
        
        // Update ALL 3 plan cards prices in the UI
        document.querySelectorAll('.subscription-plan-card').forEach(card => {
            const plan = card.dataset.plan;
            const priceAttr = `price${currency.charAt(0).toUpperCase() + currency.slice(1)}`; // priceUsd, priceEur, etc.
            const planPrice = parseFloat(card.dataset[priceAttr]);
            
            console.log(`Updating ${plan} plan price for ${currency}: ${planPrice}`); // Debug log
            
            if (!isNaN(planPrice)) {
                const priceDisplay = card.querySelector('.plan-price');
                if (priceDisplay) {
                    const currencySymbol = this.getCurrencySymbol(currency);
                    priceDisplay.innerHTML = `${currencySymbol}${planPrice.toFixed(2)}<small>/month</small>`;
                    
                    console.log(`Updated ${plan} card to show: ${currencySymbol}${planPrice.toFixed(2)}`); // Debug log
                }
            } else {
                console.warn(`No price found for ${plan} plan in ${currency}`); // Debug warning
            }
        });

        // Update the selected plan's price in input fields
        const selectedPlanCard = document.querySelector('.subscription-plan-card.selected');
        if (selectedPlanCard) {
            this.updatePriceForSelectedPlan(selectedPlanCard);
        }
        
        console.log('All plan cards updated for currency:', currency.toUpperCase()); // Debug log
    }

    // NEW METHOD: Update price when plan is selected
    updatePriceForSelectedPlan(selectedCard) {
        const currency = document.getElementById('currency').value.toLowerCase();
        const priceAttr = `price${currency.charAt(0).toUpperCase() + currency.slice(1)}`; // Convert to camelCase: priceUsd, priceEur, etc.
        
        // Get price from data attribute
        const planPrice = parseFloat(selectedCard.dataset[priceAttr]);
        
        if (!isNaN(planPrice)) {
            // Update currency symbol
            document.getElementById('amountSymbol').textContent = this.getCurrencySymbol(currency);
            
            // Update hidden input for backend
            document.getElementById('amount').value = planPrice;
            
            // Update visible display for user
            document.getElementById('amountDisplay').value = this.formatCurrency(planPrice, currency);
        }
    }

    updateSummary(){
        // const currency = document.getElementById('currency').value;
        // const amount = parseFloat(document.getElementById('amount').value);
        // const paymentMethodText = getPaymentMethodText();

        // document.getElementById('summaryMethod').textContent = paymentMethodText;
        // document.getElementById('summaryPlan').textContent = this.selectedPlan;
        // document.getElementById('summaryAmount').textContent = this.formatCurrency(amount, currency);
        // document.getElementById('summaryCurrency').textContent = currency.toUpperCase();
        // document.getElementById('summaryTotal').textContent = this.formatCurrency(amount, currency);

        const currency = document.getElementById('currency').value.toLowerCase();

        // Get amount from hidden input (always kept updated by selectSubscriptionPlan)
        const amount = parseFloat(document.getElementById('amount').value) || 0;

        document.getElementById('summaryMethod').textContent = this.getPaymentMethodText();
        document.getElementById('summaryPlan').textContent = this.selectedPlan || '-';
        document.getElementById('summaryAmount').textContent = this.formatCurrency(amount, currency);
        document.getElementById('summaryCurrency').textContent = currency.toUpperCase();
        document.getElementById('summaryTotal').textContent = this.formatCurrency(amount, currency);
    }
    getPaymentMethodText() {   
        const paymentMethods = {
            pm_card_visa: 'Visa Card',
            pm_card_mastercard: 'MasterCard',
            pm_card_amex: 'American Express',
        };
        return paymentMethods[this.selectedPaymentMethod] || 'Card';
    }

    // const currencyRates = {
    //     usd: 1,
    //     eur: 0.92,
    //     gbp: 0.80,
    //     cad: 1.35
    // };
    formatCurrency(amount, currency) {
        const symbols = {
            'usd': '$',
            'eur': '€',
            'gbp': '£',
            'cad': 'C$',
            'inr':'₹'
        };
        const symbol = symbols[currency] || '$';
        return `${symbol}${amount.toFixed(2)}`;
    }



    setupValidation() {
        const form = document.getElementById('paymentForm');
        // Remove amount input validation since it's now set by plan selection
    }



    /////////
    async processPayment() {
        // Validate form
        const form = document.getElementById('paymentForm');
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        // Get form data
        const amount = parseFloat(document.getElementById('amount').value);
        const currency = document.getElementById('currency').value;

        // Validate required data
        if (!amount || amount <= 0) {
            this.showError('Please select a subscription plan');
            return;
        }

        // Get auth data
        const token = auth.getToken();
        const currentUser = auth.getCurrentUser();
        
        if (!token || !currentUser) {
            this.showError('Authentication required. Please log in again.');
            window.location.href = 'signin.html';
            return;
        }

        // Show processing overlay
        this.showProcessingOverlay();

        try {
            // Prepare payment data
            const paymentData = {
                amount: amount,
                currency: currency,
                paymentMethodId: this.selectedPaymentMethod,
                paymentType: "gateway",
                subscriptionPlan: this.selectedPlan
            };

            console.log('Processing payment:', paymentData);

            // Make API call
            const response = await fetch(PAYMENT_API, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                    'userName': currentUser.userName
                },
                body: JSON.stringify(paymentData)
            });

            const result = await response.json();

            if (response.ok) {
                // Store subscription plan in localStorage
                const currentUser = auth.getCurrentUser();
                if (currentUser) {
                    currentUser.membershipPlan = this.selectedPlan;
                    localStorage.setItem('CURRENT_USER', JSON.stringify(currentUser));
                }
                
                this.showSuccess(result, paymentData);
                
                // Redirect to home after 3 seconds
                setTimeout(() => {
                    window.location.href = 'home.html';
                }, 3000);
            } else {
                throw new Error(result.message || 'Payment processing failed');
            }

        } catch (error) {
            console.error('Payment error:', error);
            this.showError(error.message || 'Payment processing failed. Please try again.');
        } finally {
            this.hideProcessingOverlay();
        }
    }

    showProcessingOverlay() {
        document.getElementById('processingOverlay').style.display = 'flex';
    }

    hideProcessingOverlay() {
        document.getElementById('processingOverlay').style.display = 'none';
    }

    showSuccess(result, paymentData) {
        const resultDiv = document.getElementById('paymentResult');
        const currency = paymentData.currency.toUpperCase();
        const amount = this.formatCurrency(paymentData.amount, paymentData.currency);
        
        resultDiv.innerHTML = `
            <div class="alert alert-success shadow-sm">
                <div class="d-flex align-items-center mb-3">
                    <i class="fas fa-check-circle text-success me-3" style="font-size: 2rem;"></i>
                    <div>
                        <h4 class="alert-heading mb-1">Payment Successful!</h4>
                        <p class="mb-0">Your payment has been processed successfully.</p>
                    </div>
                </div>
                
                <hr>
                
                <div class="row">
                    <div class="col-md-6">
                        <strong>Transaction ID:</strong><br>
                        <code>${result.transactionId || result.id || 'N/A'}</code>
                    </div>
                    <div class="col-md-6">
                        <strong>Amount:</strong><br>
                        ${amount} ${currency}
                    </div>
                </div>
                
                <div class="row mt-2">
                    <div class="col-md-6">
                        <strong>Payment Method:</strong><br>
                        ${this.getPaymentMethodText()}
                    </div>
                    <div class="col-md-6">
                        <strong>Plan:</strong><br>
                        ${this.selectedPlan}
                    </div>
                </div>
                
                <div class="row mt-2">
                    <div class="col-md-6">
                        <strong>Status:</strong><br>
                        <span class="badge bg-success">Completed</span>
                    </div>
                    <div class="col-md-6">
                        <strong>Billing:</strong><br>
                        Monthly Subscription
                    </div>
                </div>
                
                <div class="mt-3">
                    <button class="btn btn-primary me-2" onclick="window.print()">
                        <i class="fas fa-print me-1"></i> Print Receipt
                    </button>
                    <button class="btn btn-outline-primary" onclick="window.location.href='home.html'">
                        <i class="fas fa-home me-1"></i> Go to Home
                    </button>
                </div>
                
                <div class="mt-2 text-center">
                    <small class="text-muted">Redirecting to home page in 3 seconds...</small>
                </div>
            </div>
        `;
        
        resultDiv.style.display = 'block';
        resultDiv.scrollIntoView({ behavior: 'smooth' });
        
        // Scroll to result
        setTimeout(() => {
            resultDiv.scrollIntoView({ behavior: 'smooth' });
        }, 100);
    }

    showError(message) {
        const resultDiv = document.getElementById('paymentResult');
        
        resultDiv.innerHTML = `
            <div class="alert alert-danger shadow-sm">
                <div class="d-flex align-items-center">
                    <i class="fas fa-exclamation-triangle text-danger me-3" style="font-size: 2rem;"></i>
                    <div>
                        <h4 class="alert-heading mb-1">Payment Failed</h4>
                        <p class="mb-0">${message}</p>
                    </div>
                </div>
                
                <hr>
                
                <div>
                    <button class="btn btn-danger me-2" onclick="paymentHandler.resetForm()">
                        <i class="fas fa-redo me-1"></i> Try Again
                    </button>
                    <button class="btn btn-outline-secondary" onclick="window.location.href='home.html'">
                        <i class="fas fa-home me-1"></i> Back to Home
                    </button>
                </div>
            </div>
        `;
        
        resultDiv.style.display = 'block';
        resultDiv.scrollIntoView({ behavior: 'smooth' });
    }

    resetForm() {
        // Reset form
        document.getElementById('paymentForm').reset();
        document.getElementById('paymentForm').classList.remove('was-validated');
        
        // Reset payment method selection
        // this.selectedPaymentMethod = 'pm_card_visa';
        // document.getElementById('paymentMethodId').value = this.selectedPaymentMethod;
        
        // Reset subscription plan selection
        this.selectedPlan = 'PRO';
        document.getElementById('selectedPlan').value = this.selectedPlan;
        
        // Reset payment method UI
        document.querySelectorAll('.payment-method-card').forEach(card => {
            card.classList.remove('selected');
            const checkIcon = card.querySelector('.fa-check-circle');
            if (checkIcon) checkIcon.style.display = 'none';
        });
        
        // Select first payment method
        // const firstCard = document.querySelector('.payment-method-card');
        // if (firstCard) {
        //     this.selectPaymentMethod(firstCard);
        // }
        
        // Reset subscription plan selection
        document.querySelectorAll('.subscription-plan-card').forEach(card => {
            card.classList.remove('selected');
        });
        
        // Select PRO plan by default
        // const proCard = document.querySelector('[data-plan="PRO"]');
        // if (proCard) {
        //     this.selectSubscriptionPlan(proCard);
        // }

        // Re-initialize defaults
        this.initializeDefaults();
        
        // Hide result
        document.getElementById('paymentResult').style.display = 'none';
        
        // Update summary
        this.updateSummary();
        
        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}

    // Initialize payment handler when DOM is loaded
    let paymentHandler;
    document.addEventListener('DOMContentLoaded', () => {
        paymentHandler = new PaymentHandler();
    });

// Export for global access
window.paymentHandler = paymentHandler; 
    //////////
