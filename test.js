import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50,          // 동시 가상 유저 50명
  duration: '30s',  // 30초 동안 테스트
};

export default function () {
  const payload = JSON.stringify({
    userId: Math.floor(Math.random() * 100000),
    amount: Math.floor(Math.random() * 50000) + 1000,
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  const res = http.post('http://localhost:8080/orders/async', payload, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}