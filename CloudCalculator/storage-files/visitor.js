async function updateandDisplayVisitorCount() {
    const functionUrl = 'https://calc-linux-function-app.azurewebsites.net/api/counter_function';
    const countElement = document.getElementById('visitor-count');
    try {
        const response = await fetch(functionUrl, {
            method: 'POST',
            mode: 'cors'
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }
        const data = await response.json();

        if (data && typeof data.count !== 'undefined') {
            countElement.textContent = data.count;
        }
        else {
            console.error("Invalid response format from function:", data);
            throw new Error("Invalid response format from function.");
        }

    } catch (error) {
        console.error("Error fetching visitor or updating count:", error);
    }
}

window.addEventListener('DOMContentLoaded', (event) => {
    console.log("DOM fully loaded and parsed. Fetching visitor count...");
    updateAndDisplayVisitorCount();
});