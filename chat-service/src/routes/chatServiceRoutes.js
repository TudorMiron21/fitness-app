const express = require('express');
const router = express.Router();
const { testController,getMessages } = require('../controller/messageController');


router.get('/health', (req, res) => res.status(200).json({ status: 'UP' }));
router.get('/info', (req, res) => res.status(200).json({ info: 'Node.js service info' }));
router.get('/api/v1/chatService/',testController);


router.get(`/chatService/getMessages/:email1/:email2`, getMessages);


module.exports = router;
