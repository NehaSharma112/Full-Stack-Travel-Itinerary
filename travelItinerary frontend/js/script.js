
const AUTH_ITINERARY_PATH = 'http://localhost:8080/api/itineraries';
// generate trip suggestions by calling open ai
async function generateTripSuggestions(userInput) {
    try{
        const systemPrompt = `You are a travel advisor. Provide a concise response with ONLY the following information in this exact format:

        DESTINATION: [Main destination name]
        DURATION: [Suggested trip duration]
        BUDGET: [Estimated budget range]
        BEST_TIME: [Best time to visit]
        TOP_ATTRACTIONS: [List 3-4 main attractions, separated by semicolons]
        ACCOMMODATION: [Brief accommodation recommendation]
        LOCAL_FOOD: [2-3 must-try local dishes, separated by semicolons]
        TRAVEL_TIP: [One important travel tip]

        Keep each section brief and specific. Do not include extra formatting or explanations.`;

        const userPrompt = `Please provide essential travel information for:${userInput}`;

        const response = await callGitHubAI(`${systemPrompt}\n\nUser Request: ${userPrompt}`);

        return formatTripResponse(response);
    }catch (error) {
        console.error('Error generating trip suggestions:', error);
        throw error;
    }
       
}

async function formatAIResponse(response) {
    try{
        const lines = response.split('\n');
        const tripData = {};
        lines.forEach(line => {
            if(line.includes(":")){
                // const part = line.split(':');
                // const key = part[0];
                // const value = part[1];
                const [key, value] = line.split(':');
                if(key && value){
                    tripData[key.trim()]=value.trim();//hence store data like this tripData["destination" ]="GOA"
                }
            }
            
        });

        //now call backend
        const authToken = localStorage.getItem("JWT_TOKEN");
        const currentUser = localStorage.getItem("CURRENT_USER");
        const user = JSON.parse(currentUser);

        const backendResponse = await fetch(`${AUTH_ITINERARY_PATH}?userId=${encodeURIComponent(user.id)}`,{
            method:'POST',
            header : {
                'Content-Type':'application/json',
                'Authorization':`Bearer ${authToken}`,
                'userName':user.userName
            },
            body: JSON.stringify({
                destination: tripData.DESTINATION,
                fullItinerary: response,
                budgetRange: tripData.BUDGET
            })
        });///////////////////////////////
    }
    
}