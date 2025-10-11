const axios = require('axios');

const TARGET = process.env.TARGET;

async function callEcho() {
  try {
    const res = await axios.get(TARGET, {
      timeout: 60000 // 60s
    });
    console.log(`[${new Date().toISOString()}] Response:`, res.data);
  } catch (err) {
    if (err.response) {
      console.error(`[${new Date().toISOString()}] HTTP ${err.response.status}`, err.response.data);
    } else {
      console.error(`[${new Date().toISOString()}] Error:`, err.message);
    }
  }
}

setInterval(callEcho, 5000);
console.log(`🚀 Node client started. Calling ${TARGET} every 5s...`);