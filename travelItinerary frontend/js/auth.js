//authentication module
const Auth_Api = 'http://localhost:8080/api/auth';
const GOOGLE_CLIENT_ID ='310671165087-7ainrptl0n2p93h32kno62keept3r8nj.apps.googleusercontent.com'

const auth={
    // login,
    // signup,
    // request,
    // logout,
    // isAuthenticated,
    // getCurrentUser,
    // getToken
    // Google OAuth buttons
    signInWithGoogle() {
        try {
            if (typeof google === 'undefined' || !google.accounts || !google.accounts.oauth2) {
                throw new Error('Google OAuth library not loaded');
            }

            // Check if we're in a development environment
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.warn('Running on localhost - Google OAuth might have domain restrictions');
            }

            // Use OAuth2 popup approach for better user experience
            const client = google.accounts.oauth2.initTokenClient({
                client_id: GOOGLE_CLIENT_ID,
                scope: 'openid email profile',
                callback: (response) => {
                    if (response.access_token) {
                        console.log(response.access_token);
                        this.handleGoogleOAuthResponse(response.access_token);
                    } else {
                        console.error('No access token received');
                        alert('Google sign-in failed. Please try again.');
                    }
                },
                error_callback: (error) => {
                    console.error('Google OAuth error:', error);
                    if (error.type !== 'popup_closed') {
                        alert('Google sign-in failed: ' + error.type);
                    }
                },
                ux_mode: 'popup',
                select_account: true // This forces account selection
            });

            // Request access token (opens popup)
            client.requestAccessToken();

        } catch (error) {
            console.error('Error with Google Sign-In:', error);
            this.handleGoogleNotAvailable();
        }
    },

    // Handle Google OAuth response with access token  calling google's api
    async handleGoogleOAuthResponse(access_token){
        try{
            //fetch user info using access token
            //method if not define then it is by default GET
            const userInfoResponse = await fetch('https://www.googleapis.com/oauth2/v2/userinfo',{
                headers:{
                    'Authorization': `Bearer ${access_token}`
                }
            });

            if (!userInfoResponse.ok) {
                throw new Error('Failed to fetch user info');
            }

            const userData = await userInfoResponse.json();//converts response in json form
            console.log('Google user data:', userData);

            if(userData && userData.email){//if valid user data comes
                // Pass both user data and the Google access token to backend
                await this.processGoogleUser(userData,access_token);
            }else{
                throw new Error('Invalid Google User Data');
            }

        }catch(error){
            console.error('Google OAuth response error:', error);
            alert('Failed to process Google authentication: ' + error.message);
        }
    },

    // Handle case when Google Sign-In is not available     in case of failure of google sign in
    handleGoogleNotAvailable() {
        console.warn('Google Sign-In not available');
        
        if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            alert('Google Sign-In is not configured for localhost development.\n\nTo use Google Sign-In:\n1. Add http://localhost:5500 and http://127.0.0.1:5500 to authorized origins in Google Cloud Console\n2. Enable both "Authorized JavaScript origins" and "Authorized redirect URIs"\n3. Or use email/password login for development');
        } else {
            alert('Google Sign-In is not available. Please use email/password login or try again later.');
        }
    },

    async processGoogleUser(userData, access_token){
        try{
            console.log('Attempting Google authentication for:', userData.email);

            const response = await fetch(`${Auth_Api}/google-auth`,{
                method:'POST',
                headers:{
                    'Content-Type':'application/json',
                    'request-Type':'google'
                },
                body:JSON.stringify({
                    token:googleToken,
                    userData:userData//comes from token in userInfoResponse.json()
                })
            });

            const data = await response.json();//Parses the backend response
            /*{
                "status": "success",
                "data": {
                    "token": "your-jwt-token",
                    "id": 123,
                    "email": "user@example.com",
                    "userName": "john_doe",
                    "firstName": "John",
                    "lastName": "Doe",
                    "role": "user"
                }
                }
            */

            if(response.ok && data.data){
                localStorage.setItem('JWT_TOKEN',data.token);
                localStorage.setItem('CURRENT_USER',JSON.stringify({
                    id:data.data.id,
                    firstName:data.data.firstName,//userData.firstName = userData.given_name;
                    lastName:data.data.lastName,//userData.lastName = userData.family_name;
                    userName:data.data.userName,
                    email:data.data.email,
                    role:data.data.role
                }));

                const normalizedUser = normalizeGoogleUser(userData);
                await this.processGoogleUser(normalizedUser, accessToken);
                console.log('Google authentication successful');
                alert('Google authentication successful!');
                window.location.href = 'http://127.0.0.1:5500/html/home.html';
                return;
            }else{
                throw new Error(data.message || 'Google authentication failed');
            }
            

        }catch(error){
            console.error('Error processing Google user:', error);
            alert('Google authentication failed: ' + error.message);
        }
    },
    function normalizeGoogleUser(googleUser) {
    return {
        id: googleUser.id,
        email: googleUser.email,
        firstName: googleUser.given_name,
        lastName: googleUser.family_name,
        fullName: googleUser.name,
        userName: googleUser.email?.split('@')[0] || '',
        picture: googleUser.picture || '',
    };
}

    async login(emailOrUsername, password){
        const isEmail = emailOrUsername.includes('@');
        const loginData = isEmail ? {email : emailOrUsername, password}:{userName : emailOrUsername, password};
//fetch(input: RequestInfo | URL, init?: RequestInit): Promise<Response>;
        const response= await fetch('${Auth_Api}/login',
            {
                method : 'POST',
                headers : { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData)
            }
        );

        const data = await response.json();

        if(response.ok){
            localStorage.setItem('JWT_TOKEN', data.token);
            localStorage.setItem('CURRENT_USER',JSON.stringify(
                {
                    id: data.id,
                    userName: data.userName,
                    email: data.email,
                    firstName: data.firstName,
                    lastName: data.lastName,
                    role: data.role
                }
            ));
            return data;
        }
        throw new Error(data.message || 'Login Failed');
        
    },



    async signup(userdata){
        const response = await fetch('${Auth_Api}/signup',
            {
                method : 'POST',
                headers:{'Content-Type':'application/json'},
                body : JSON.stringify(
                    {
                        usename: userdata.email,
                        email:userdata.email,
                        password: userdata.password,
                        firstName:userdata.firstName,
                        lastName:userdata.lastName
                    }
                )
            }
        );

        const data = await response.json();
        if(response.ok){
            localStorage.setItem('JWT_TOKEN', data.token);
            localStorage.setItem('CURRENT_USER',JSON.stringify(
                {
                    id: data.id,
                    userName: data.userName,
                    email: data.email,
                    firstName: data.firstName,
                    lastName: data.lastName,
                    role: data.role
                }
            ));
            return data;
        }
        throw new Error(data.message || 'Signup Failed');

    },

    async request(url, options={}){//it is like helper function
        const token = localStorage.getItem('JWT_TOKEN');
        return fetch(url,{
            ...options,//conatins method and body
            headers: {
                'Content-Type':'application/json',
                ...(token && {'Authorization': `Bearer ${token}`}), //if token exist && it is in this pattern Bearer bdjgbjgbdk
                ...options.headers//if extra options are given in header then include them also
            }
        });
    },

    logout(){
        localStorage.removeItem('JWT_TOKEN');
        localStorage.removeItem('CURRENT_USER');
        window.location.href ='http://127.0.0.1:5500/html/sign-in.html';//redirect it on login page
    },

    isAuthenticated(){
        return localStorage.getItem('JWT_TOKEN') !== null;//if not nulll then return true
    },

    getCrrentUser(){
        const user = localStorage.getItem('CURRENT_USER');
        return user ? JSON.parse(user): null;
    },

    getToken(){
        return localStorage.getItem('JWT_TOKEN');       
    }


};

document.addEventListener('DOMContentLoaded',() => {
    const loginForm = document.getElementById('signinForm');
    if(loginForm){
        loginForm.onsubmit = async(e)=>{
            e.preventDefault();

            const emailOrUsername = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            if(!emailOrUsername || !password){
                alert("All fields are required");
                return;                
            }

            //we have username password
            try{
                await auth.login(emailOrUsername,password);
                alert("Login Successful");
                window.location.href = "http://127.0.0.1:5500/html/home.html";
            }catch(error){
                alert("Login failed "+error.message);
            }
        };
    }

    const signupForm = document.getElementById('signupForm');

    if(signupForm){
        signupForm.onsubmit = async(e)=>{
            e.preventDefault();

            try{
                await auth.signup({
                    firstName:document.getElementById('firstName').value,
                    lastName:document.getElementById('lastName').value,
                    email:document.getElementById('email').value,
                    password:document.getElementById('password').value
                });

                alert("Account created successfully...!!!");
                window.location.href= "http://127.0.0.1:5500/html/home.html";
            }catch(error){
                alert("Sign-up Failed "+error.message);
            }
        };
    }
    // Logout buttons
    document.querySelectorAll('[data-logout]').forEach(btn => {
        btn.onclick = () => auth.logout();
    });

    // Google OAuth buttons
    document.querySelectorAll('.google-signin-btn').forEach(btn => {
        btn.onclick = (e) => {
            e.preventDefault();
            auth.signInWithGoogle();
        };
    });

    // Route protection
    if (window.location.pathname.includes('home.html')) {
        if (!auth.isAuthenticated()) {
            window.location.href = 'http://127.0.0.1:5500/html/signin.html';
        }
    }
});