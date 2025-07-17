
function encryptToken(token) {
    return btoa(token.split('').map(char => String.fromCharCode(char.charCodeAt(0) + 9)).join(''));
}

function decryptToken(encrypted) {
    return atob(encrypted).split('').map(char => String.fromCharCode(char.charCodeAt(0) - 9)).join('');
}

const ENCRYPTED_GITHUB_TOKEN = "cHJ9cX5raHlqfWg6OkpAUl1RW0o5TVt9cYNhYnp7UFJAaEtReW46XFo6PEt5g2B1cVBBU1BZV1I5az05dVQ+Qnteb39tX1CCd0pzgFtePV9QTjtfUEFbPHZtXGFj";
const ENDPOINT = "https://models.github.ai/inference";
const MODEL = "openai/gpt-4.1";

async function callGitHubAI(prompt) {
    try {
        const GITHUB_TOKEN = decryptToken(ENCRYPTED_GITHUB_TOKEN);
        
        const response = await fetch(`${ENDPOINT}/chat/completions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${GITHUB_TOKEN}`
            },
            body: JSON.stringify({
                messages: [
                    { role: "system", content: "You are a helpful assistant." },
                    { role: "user", content: prompt }
                ],
                temperature: 1,
                top_p: 1,
                model: MODEL
            })
        });

        if (!response.ok) {
            throw new Error(`API call failed: ${response.status} ${response.statusText}`);
        }

        const data = await response.json();//parsing into json
        
        if (data.choices && data.choices.length > 0) {
            return data.choices[0].message.content;
        } else {
            throw new Error('No response content received from API');
        }
    } catch (error) {
        console.error('Error calling GitHub AI API:', error);
        throw error;
    }
}
