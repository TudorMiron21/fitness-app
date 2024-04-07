import React, { useEffect, useState } from "react";
import io from "socket.io-client";
import { useNavigate } from "react-router-dom";
import { validateToken } from "../utils/auth";
import { NavBar } from "../NavBarComponents/NavBar.jsx";
import { Footer } from "../FooterComponent/Footer.jsx";
import { jwtDecode } from "jwt-decode";
import { Chat } from "./ChatContainer.jsx";
import axios from "axios";
import "./ChatPage.css";
const SERVER_URL = "http://localhost:8084";
let socket;
export const ChatPage = () => {
  const navigate = useNavigate();

  const [subscribers, setSubscribers] = useState([]);
  const [room, setRoom] = useState("");
  const [showChat, setShowChat] = useState(false);
  const [coachEmail, setCoachEmail] = useState("");
  const [destinationEmail, setDestinationEmail] = useState("");

  useEffect(() => {
    const verifyAndConnect = async () => {
      const token = localStorage.getItem("access_token");

      if (!token) {
        navigate("/login");
        return;
      }

      const isTokenValid = await validateToken(token);

      if (!isTokenValid) {
        navigate("/login");
        return;
      }

      // Connect to the socket after token validation
      socket = io.connect(SERVER_URL, {
        extraHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });

      socket.on("connect_error", (error) => {
        console.error("Connection Error:", error);
      });

      socket.on("connect_timeout", (timeout) => {
        console.error("Connection Timeout:", timeout);
      });

      console.log(socket);
    };

    verifyAndConnect();

    const getSubscribers = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/adminCoachService/coach/getSubscribers",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("access_token")}`,
            },
          }
        );
        setSubscribers(response.data);
      } catch (error) {
        console.error("Error fetching subscribers:", error);
      }
    };

    getSubscribers();

    // Cleanup on unmount
    return () => {
      if (socket) {
        socket.disconnect();
      }
    };
  }, [navigate]);

  const handleJoinRoomClick = async (subscriber) => {
    const token = localStorage.getItem("access_token");
    if (!token) {
      return;
    }
    const decodedToken = jwtDecode(token);
    setCoachEmail(decodedToken.sub);
    setDestinationEmail(subscriber.email);
    const roomName = `${decodedToken.sub}_${subscriber.email}`;
    console.log(roomName);
    setRoom(roomName);
    socket.emit("join_private_chat", roomName);

    setShowChat(true);
  };
  return (
    <div>
      <NavBar />
      <div className="chat-page-container">
        <div className="members-list">
          <h2>Members</h2>
          <ul>
            {subscribers.map((subscriber) => (
              <li key={subscriber.id}>
                <div
                  className="subscriber-container"
                  onClick={() => handleJoinRoomClick(subscriber)}
                >
                  {subscriber.email}
                </div>
              </li>
            ))}
          </ul>
        </div>
        <div className="chat-room">
          {socket && showChat ? (
            <Chat
              socket={socket}
              source_email={coachEmail}
              destination_email={destinationEmail}
              room={room}
            />
          ) : null}
        </div>
      </div>
      <Footer />
    </div>
  );
};
