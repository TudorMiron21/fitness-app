import React, { useEffect, useState } from "react";
import ScrollToBottom from "react-scroll-to-bottom";
import "./ChatContainer.css";
import axios from "axios";

export const Chat = ({ socket, source_email, destination_email, room }) => {
  const [currentMessage, setCurrentMessage] = useState("");
  const [messageList, setMessageList] = useState([]);

  const sendMessage = async () => {
    if (currentMessage !== "") {
      // const currentTime = new Date().toLocaleTimeString([], {
      //   hour: "2-digit",
      //   minute: "2-digit",
      // });
      const currentTime = new Date().toISOString();
      const messageData = {
        room: room,
        source_email: source_email,
        destination_email: destination_email,
        text_content: currentMessage,
        timestamp: currentTime,
      };

      console.log(messageData);

      await socket.emit("send_message", messageData);
      setMessageList((list) => [...list, messageData]);
      setCurrentMessage("");
    }
  };

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8084/chatService/getMessages/${source_email}/${destination_email}`
        );
        const sortedMessages = response.data.messages.sort((a, b) => {
          const dateA = new Date(a.timestamp);
          const dateB = new Date(b.timestamp);
          return dateA - dateB;
        });
        console.log(response.data);
        setMessageList(sortedMessages);
      } catch (error) {
        console.error("Error fetching messages:", error);
      }
    };

    fetchMessages();

    socket.on("receive_message", (data) => {
      console.log(data);
      setMessageList((list) => [...list, data]);
    });
  }, [socket]);

  return (
    <div className="chat-window">
      <div className="chat-header">
        <p>Live Chat</p>
      </div>
      <div className="chat-body">
        <ScrollToBottom className="message-container">
          {messageList.map((messageContent) => {
            const messageDate = new Date(messageContent.timestamp);
            const today = new Date();
            const isToday = messageDate.toDateString() === today.toDateString();

            // Format time
            const timeString = messageDate.toLocaleTimeString("en-US", {
              hour: "2-digit",
              minute: "2-digit",
              hour12: true,
            });

            // Construct the date string: if the message was sent today, display "Today"
            const dateString = isToday
              ? "Today"
              : messageDate.toLocaleDateString("en-US", {
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                });

            console.log(`${dateString} at ${timeString}`);

            return (
              <div
                className="message"
                id={
                  source_email === messageContent.source_email ? "you" : "other"
                }
                key={messageContent._id} // Assuming each message has a unique `_id`
              >
                <div>
                  <div className="message-content">
                    <p>{messageContent.text_content}</p>
                  </div>
                  <div className="message-meta">
                    <p id="time">{`${dateString} at ${timeString}`}</p>
                    <p id="author">{messageContent.source_email}</p>
                  </div>
                </div>
              </div>
            );
          })}
        </ScrollToBottom>
      </div>
      <div className="chat-footer">
        <input
          type="text"
          value={currentMessage}
          placeholder="Hey..."
          onChange={(event) => {
            setCurrentMessage(event.target.value);
          }}
          onKeyPress={(event) => {
            event.key === "Enter" && sendMessage();
          }}
        />
        <button onClick={sendMessage}>&#9658;</button>
      </div>
    </div>
  );
};
