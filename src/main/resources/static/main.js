const socket = new WebSocket('ws://localhost:8080/ws');

const canvas = document.getElementById('screen');
const ctx = canvas.getContext('2d');

socket.binaryType ="arraybuffer";

socket.onopen = () => {
    console.log('WebSocket connection established');
};

socket.onmessage = (event) => {
    console.log('Message received');
    const imageData = new Uint8Array(event.data);

    const blob = new Blob([imageData], { type: "image/bmp" });
    const url = URL.createObjectURL(blob);

    const img = new Image();

    img.onload = () => {
        console.log("image loaded");

        canvas.width = img.width;
        canvas.height = img.height;

        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

        URL.revokeObjectURL(url);
    };

    img.onerror = () => {
        console.error("image not loaded", img.src);
        console.error("Length: ", imageData.length);
        URL.revokeObjectURL(url);
    };

    img.src = url;
};

socket.onclose = () => {
    console.log('WebSocket connection closed');
};

socket.onerror = (error) => {
    console.error('WebSocket error:', error);
};

document.addEventListener('keydown', (event) => {
    sendKeyEvent('keydown', event.code, event.key);
})

document.addEventListener('keyup', (event) => {
    sendKeyEvent('keyup', event.code, event.key);
})

canvas.addEventListener('mousedown', (event) => {
    const { x, y } = getRelativeCoordinates(event);
    sendMouseEvent('mousedown', x, y, event.button);
});

canvas.addEventListener('mouseup', (event) => {
    const { x, y } = getRelativeCoordinates(event);
    sendMouseEvent('mouseup', x, y, event.button);
});

canvas.addEventListener('mousemove', (event) => {
    const { x, y } = getRelativeCoordinates(event);
    sendMouseEvent('mousemove', x, y);
});

function getRelativeCoordinates(event) {
    const rect = canvas.getBoundingClientRect();
    return {
        x: ((event.clientX - rect.left) * canvas.width) / rect.width,
        y: ((event.clientY - rect.top) * canvas.height) / rect.height
    };
}

function sendKeyEvent(action, code, key) {
    const message = JSON.stringify({
        type: 'keyboard',
        action: action,
        code: code, // 키보드의 물리적 위치
        key: key    // 키의 실제 문자 값
    });
    socket.send(message);
}

function sendMouseEvent(action, x, y, button = null) {
    const message = {
        type: 'mouse',
        action: action,
        x: Math.round(x),
        y: Math.round(y)
    };
    if (button !== null) {
        message.button = button;
    }
    socket.send(JSON.stringify(message));
}