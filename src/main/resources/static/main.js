const socket = new WebSocket('ws://localhost:8080/ws');

const canvas = document.getElementById('screen');
const ctx = canvas.getContext('2d');

canvas.width = 1;
canvas.height = 1;

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

canvas.addEventListener('wheel', (event) => {
    const { x, y } = getRelativeCoordinates(event);
    sendWheelEvent('wheel', event.deltaX, event.deltaY, x, y);
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

function sendWheelEvent(action, deltaX, deltaY, x, y) {
    const message = {
        type: 'wheel',
        action: action,
        deltaX: Math.round(deltaX),
        deltaY: Math.round(deltaY),
        x: Math.round(x),
        y: Math.round(y)
    };

    if (socket.readyState === WebSocket.OPEN)
            socket.send(JSON.stringify(message));
}

// Delta 데이터를 화면에 적용
function applyDelta(deltaData) {
    const delta = new Uint8Array(deltaData); // delta 데이터를 Uint8Array로 변환
    let x, y, r, g, b;

    // 바운딩 박스 좌표 추출
    let minX = (delta[0] << 8) | delta[1];
    let minY = (delta[2] << 8) | delta[3];
    let maxX = (delta[4] << 8) | delta[5];
    let maxY = (delta[6] << 8) | delta[7];

    //console.log('minX:', minX);
    //console.log('minY:', minY);
    //console.log('maxX:', maxX);
    //console.log('maxY:', maxY);
    
    if(canvas.width < maxX){
		canvas.width = maxX + 1;
	}
	if(canvas.height < maxY){
		canvas.height = maxY + 1;
	}

    // 바운딩 박스 크기 계산
    let width = maxX - minX + 1;  // +1을 해서 실제 크기 반영
    let height = maxY - minY + 1; // +1을 해서 실제 크기 반영

    // 바운딩 박스 크기만큼의 ImageData 생성
    let imgData = ctx.createImageData(width, height);

    // Delta 데이터를 바운딩 박스를 기준으로 처리
    for (let i = 8; i < delta.length; i += 7) {
        x = (delta[i] << 8) | delta[i + 1]; // x 좌표
        y = (delta[i + 2] << 8) | delta[i + 3]; // y 좌표
        r = delta[i + 4]; // R 값
        g = delta[i + 5]; // G 값
        b = delta[i + 6]; // B 값

        // 픽셀 인덱스 계산
        const pixelIndex = ((y - minY) * width + (x - minX)) * 4;
        imgData.data[pixelIndex] = r;
        imgData.data[pixelIndex + 1] = g;
        imgData.data[pixelIndex + 2] = b;
        imgData.data[pixelIndex + 3] = 255;  // Alpha는 255로 설정
    }

    // 변경된 부분만 업데이트
    ctx.putImageData(imgData, minX, minY);
}

