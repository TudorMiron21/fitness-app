import React, { useEffect,useState } from "react";
import io from "socket.io-client";
import { useNavigate } from "react-router-dom";
import { validateToken } from "../utils/auth";

const SERVER_URL = "http://localhost:8084";

export const ChatPage = () => {
  const navigate = useNavigate();

  const [payingUsers,setPayingUsers] = useState([]);
  
  useEffect(() => {
    let socket;

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

    // Cleanup on unmount
    return () => {
      if (socket) {
        socket.disconnect();
      }
    };
  }, [navigate]);

  return (
    <div>
      <h1>Chat Room</h1>
    </div>
  );
};
