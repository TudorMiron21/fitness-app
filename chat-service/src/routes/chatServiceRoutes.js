const express = require('express');
const router = express.Router();
const { testController,sendMessage } = require('../controller/messageController');


router.get('/health', (req, res) => res.status(200).json({ status: 'UP' }));
router.get('/info', (req, res) => res.status(200).json({ info: 'Node.js service info' }));
router.get('/api/v1/chatService/',testController);


router.post(`/api/v1/chatService/postMessage/:senderId/:receiverId`, sendMessage);


module.exports = router;
