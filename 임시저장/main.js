const socket = new WebSocket('ws://localhost:8080/ws');

const canvas = document.getElementById('screen');
const ctx = canvas.getContext('2d');

socket.binaryType = "arraybuffer";

let previousImage = null;  // 이전 이미지를 저장할 변수

socket.onopen = () => {
    console.log('WebSocket connection established');
    const user = localStorage.getItem('user');
    const pw = localStorage.getItem('pw');
    const ip = localStorage.getItem('ip');
    sendLoginEvent("login", user, pw, ip);
};

socket.onmessage = (event) => {
    const deltaData = new Uint8Array(event.data);  // delta 데이터 수신

    if (deltaData.length > 0) {
        // Delta 압축된 데이터 처리
        applyDelta(deltaData);
    }
};

socket.onclose = () => {
    console.log('WebSocket connection closed');
};

socket.onerror = (error) => {
    console.error('WebSocket error:', error);
};

document.addEventListener('keydown', (event) => {
    sendKeyEvent('keydown', event.code, event.key);
});

document.addEventListener('keyup', (event) => {
    sendKeyEvent('keyup', event.code, event.key);
});

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

// 상대 좌표 계산
function getRelativeCoordinates(event) {
    const rect = canvas.getBoundingClientRect();
    return {
        x: ((event.clientX - rect.left) * canvas.width) / rect.width,
        y: ((event.clientY - rect.top) * canvas.height) / rect.height
    };
}

function sendLoginEvent(action, user, pw, ip) {
    const message = JSON.stringify({
        type: 'login',
        action: action,
        user: user,
        pw: pw,
        ip: ip
    });
    socket.send(message);
}

function sendKeyEvent(action, code, key) {
    const message = JSON.stringify({
        type: 'keyboard',
        action: action,
        code: code,
        key: key
    });

    if (socket.readyState === WebSocket.OPEN)
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
    if (socket.readyState === WebSocket.OPEN)
        socket.send(JSON.stringify(message));
}

// Delta 데이터를 화면에 적용
function applyDelta(deltaData) {
    const delta = new Uint8Array(deltaData);
    let x, y, r, g, b;
    let imgData = ctx.createImageData(canvas.width, canvas.height);
    

    // Delta 데이터를 바운딩 박스를 기준으로 처리
    for (let i = 0; i < delta.length; i += 5) {
        x = delta[i]; // x 좌표
        y = delta[i + 1]; // y 좌표
        r = delta[i + 2]; // R 값
        g = delta[i + 3]; // G 값
        b = delta[i + 4]; // B 값

        const pixelIndex = (y * canvas.width + x) * 4;
        imgData.data[pixelIndex] = r;
        imgData.data[pixelIndex + 1] = g;
        imgData.data[pixelIndex + 2] = b;
        imgData.data[pixelIndex + 3] = 255;  // Alpha는 255로 설정
    }

    // 변경된 부분만 업데이트
    ctx.putImageData(imgData, 0, 0);
}
