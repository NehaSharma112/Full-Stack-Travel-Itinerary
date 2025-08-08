const PAYMENT_API = 'http://localhost:8080/api/payment/process';
class PaymentHandler{
    constructor(){
        this.selectedPaymentMethod='';
        this.selectedPlan='';
        this.init();
    }

    //calls init to attach listeneres
    init(){
        this.setEventListeners();
        this.updateSummary();
    }

    setEventListeners(){
        //selection of payment method and saved into variable
        document.querySelectorAll('.payment-method-card').forEach(card=>{ 
            card.addEventListener('click',(event)=>{
                this.selectedPaymentMethod(event.currentTarget);
            })
        });

        //subsription selection
        document.querySelectorAll('.subscription-plan-card').forEach(card => {
            card.addEventListener('click', (e) => {
                this.selectSubscriptionPlan(e.currentTarget);
            });
        });

        document.getElementById('currency').addEventListener('change', () => this.updateSummary());//currency change and call updateSummary

        //form submission
        document.getElementById('paymentForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.processPayment();
        });

        // Real-time validation
        this.setupValidation();
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
        
        // Update amount based on selected plan
        const planPrice = parseFloat(selectedCard.dataset.price);
        document.getElementById('amount').value = planPrice;
        
        this.updateSummary();
    }

    selectSubscriptionPlan(selectedCard) {
        // Remove selection from all plan cards
        document.querySelectorAll('.subscription-plan-card').forEach(card => {
            card.classList.remove('selected');
        });

        // Add selection to clicked card
        selectedCard.classList.add('selected');

        // Update selected plan
        this.selectedPlan = selectedCard.dataset.plan;
        document.getElementById('selectedPlan').value = this.selectedPlan;
        
        // Update amount based on selected plan
        const planPrice = parseFloat(selectedCard.dataset.price);
        document.getElementById('amount').value = planPrice;
        
        this.updateSummary();
    }


    formatCurrency(amount, currency) {
        const symbols = {
            'usd': '$',
            'eur': '€',
            'gbp': '£',
            'cad': '$'
        };
        const symbol = symbols[currency] || '$';
        return `${symbol}${amount.toFixed(2)}`;
    }

    setupValidation() {
        const form = document.getElementById('paymentForm');
        // Remove amount input validation since it's now set by plan selection
    }
}