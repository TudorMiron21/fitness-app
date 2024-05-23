import axios from 'axios';

export const validateToken = async (token) => {
  try {
    const response = await axios.get(`https://www.fit-stack.online/api/v1/auth/validateToken?token=${encodeURIComponent(token)}`);
    return response.data;
  } catch (error) {
    console.error("Error validating token:", error);
    return false;
  }
};