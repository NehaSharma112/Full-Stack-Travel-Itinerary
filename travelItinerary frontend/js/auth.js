//authentication module
const Auth_Api = 'http://localhost:8080/api/auth';

const auth={
    // login,
    // signup,
    // request,
    // logout,
    // isAuthenticated,
    // getCurrentUser,
    // getToken
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

    // Route protection
    if (window.location.pathname.includes('home.html')) {
        if (!auth.isAuthenticated()) {
            window.location.href = 'http://127.0.0.1:5500/html/signin.html';
        }
    }
});