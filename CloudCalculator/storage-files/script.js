const display = document.getElementById('display');
const historyPanel = document.getElementById('history-panel');
const historyClearButton = document.getElementById('history-clear');

let currentInput = '';
let firstOperand = null;
let secondOperand = null;
let operator = '';
let history = [];
let result = null;
let lastActionWasEquals = false;
let isError = false;

function handleNumberClick(value) {
    if (isError) {
        return;
    }
    currentInput += value;
    updateDisplay(currentInput);
}

function handleOperatorClick(operatorInput) {
    if (isError) {
        return;
    }

    if (operatorInput === '-' && currentInput === '' && firstOperand === null) {
        currentInput = '-';
        return;
    }

    if (currentInput === '' && firstOperand !== null) {
        operator = operatorInput;
        return;
    }
    
    if (firstOperand === null && currentInput !== '') {
        firstOperand = parseFloat(currentInput);
        operator = operatorInput;
        currentInput = '';
        return;
    }
    if (firstOperand !== null && currentInput !== '') {
        secondOperand = parseFloat(currentInput);
        let prevResult = firstOperand;
        calculate();
        if (!isError && result !== null) {
            if (prevResult !== null) {
                history.push(result);
                updateHistoryPanel();
            }
        }
        lastActionWasEquals = false;
        firstOperand = result;
    }

    operator = operatorInput;
    currentInput = '';
}

function handleEqualsClick() {
    if (isError || operator === '' || currentInput === '') {
        return;
    }

    if (currentInput !== '') {
        secondOperand = parseFloat(currentInput);
        calculate();
        if (!isError && result !== null) {
            history.push(result);
            updateHistoryPanel();
        }
    }
    firstOperand = result;  
    secondOperand = null;
    operator = '';
    currentInput = '';
    lastActionWasEquals = true;
}

function handleDotClick() {
    if (isError) {
        return;
    }

    if (!currentInput.includes('.')) {
        currentInput += '.';
        updateDisplay(currentInput);
    }
}

function handleClearClick() {
    isError = false;
    currentInput = '';
    firstOperand = null;
    secondOperand = null;
    operator = '';
    result = null;
    updateDisplay('0');

}

function updateDisplay(value) {
    display.value = value;
}

function updateHistoryPanel() {
    historyPanel.innerHTML = '';
    history.forEach(item => {
        const historyItem = document.createElement('li');
        historyItem.textContent = item;
        historyItem.addEventListener('click', () => {
            if (currentInput === '' || lastActionWasEquals) {
                currentInput = item.toString();
                updateDisplay(currentInput);
            }
        });
        historyPanel.appendChild(historyItem);
    });
}

function clearHistory() {
    history = [];
    updateHistoryPanel();
}

function calculate() {
    if (currentInput[currentInput.length - 1] === '.' || firstOperand === null || secondOperand === null || operator === '') {
        isError = true;
        updateDisplay('Error');
        history.push('Error');
        updateHistoryPanel();
        return;
    }

    if (operator === '+') {
        result = firstOperand + secondOperand;
    } 
    else if (operator === '-') {
        result = firstOperand - secondOperand;
    } 
    else if (operator === 'X') {
        result = firstOperand * secondOperand;
    } 
    else if (operator === '/') {
        if (secondOperand === 0) {
            result = 'Error';
            isError = true;
        } 
        else {
            result = firstOperand / secondOperand;
        }
    } 
    else {
        result = 'Error';
        isError = true;
    }

    if (isError) {
        updateDisplay('Error');
        history.push('Error')
        updateHistoryPanel();
    }
    else {
        updateDisplay(result);
    }
}

document.querySelectorAll('.number').forEach(button => {
    button.addEventListener('click', () => handleNumberClick(button.textContent));
});

document.querySelectorAll('.operator').forEach(button => {
    button.addEventListener('click', () => handleOperatorClick(button.textContent));
});

const equal = document.querySelector('.equals');
equal.addEventListener('click', handleEqualsClick);

const dot = document.querySelector('.dot');
dot.addEventListener('click', handleDotClick);

const clear = document.querySelector('.clear');
clear.addEventListener('click', handleClearClick);

historyClearButton.addEventListener('click', clearHistory);

document.addEventListener('keydown', (event) => {
    const key = event.key;

    if (key === 'Enter') {
        event.preventDefault();
        handleEqualsClick();
        return;
    }

    if (/^[0-9]$/.test(key)) {
        handleNumberClick(key);
    }
    else if (key === '+') {
        handleOperatorClick('+');
    }
    else if (key === '-') {
        handleOperatorClick('-');
    }
    else if (key === '*') {
        handleOperatorClick('X');
    }
    else if (key === '/') {
        event.preventDefault();
        handleOperatorClick('/');
    }
    else if (key === '.') {
        handleDotClick();
    }
    else if (key === 'c') {
        event.preventDefault();
        handleClearClick();
    }
});

updateDisplay('0');