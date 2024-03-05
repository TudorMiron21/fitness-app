import React, { useState, useEffect } from "react";
import "./CoachDetailsPage.css"; // Make sure to update your CSS file with the styles provided below
import { validateToken } from "../utils/auth";
import {useNavigate } from "react-router-dom";
import axios from "axios";

export const CoachDetails = () => {
  const [formData, setFormData] = useState({
    certificationType: "",
    yearsOfExperience: "",
    certificateFile: null,
  });

  const [previewSrc, setPreviewSrc] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isTokenValid,setIsTokenValid] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const checkToken = async () => {
      const token = localStorage.getItem("access_token");

      if (token) {
        setIsTokenValid(await validateToken(token));
        console.log(isTokenValid);
      }

      setIsLoading(false);
    };
    checkToken();
  }, []);

  if (isLoading) {
    // Render a loading spinner or return null if you don't want to render anything
    return <div>Loading...</div>;
  }

  if(!isTokenValid)
  {
    navigate('/register')
    return;
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setFormData({
      ...formData,
      certificateFile: file,
    });

    // File preview
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreviewSrc(reader.result);
    };
    if (file) {
      reader.readAsDataURL(file);
    } else {
      setPreviewSrc("");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    // Create an instance of FormData
    const formDataToSend = new FormData();
    formDataToSend.append('certificationType', formData.certificationType);
    formDataToSend.append('yearsOfExperience', formData.yearsOfExperience);
    formDataToSend.append('certificateImg', formData.certificateFile); // Assuming 'certificateImg' is the field name expected by the backend
  
    try {
      // Send the request with the form data
      const response = await axios.post(
        "http://localhost:8080/api/v1/adminCoachService/coach/uploadCoachDetails",
        formDataToSend,
        {
          headers: {
            'Content-Type': 'multipart/form-data', 
            'Authorization': `Bearer ${localStorage.getItem('access_token')}`
          },
        }
      );
  
      console.log(response.data);

      navigate('/home-page');

      alert('Coach details uploaded successfully.');
  
    } catch (error) {
      // Handle any errors here
      console.error('Error uploading coach details:', error);
      if (error.response) {
        // The request was made and the server responded with a status code that falls out of the range of 2xx
        console.error(error.response.data);
        console.error(error.response.status);
        console.error(error.response.headers);
        alert(`Error: ${error.response.data}`);
      } else if (error.request) {
        // The request was made but no response was received
        console.error(error.request);
        alert('No response received.');
      } else {
        console.error('Error', error.message);
        alert(`Error: ${error.message}`);
      }
    }
  };

  return (
    <div className="coach-details-page">
      <div className="coach-details-container">
        <h2 className="form-title">Coach Details</h2>
        <div className="coach-explanation">
          <p>
            We are committed to ensuring the highest standards of coaching for
            our clients. Uploading your certification helps us verify your
            credentials and maintain a trusted network of certified
            professionals. Your years of experience and certification type will
            also help potential clients to better understand your expertise and
            background.
          </p>
          <p>
            Please note that your privacy is paramount to us. Any documents or
            information you provide will be handled with the strictest
            confidence and used solely for the purpose of verifying your
            qualifications. If you have any questions or concerns about our
            privacy practices, please do not hesitate to contact us.
          </p>
        </div>
        <form onSubmit={handleSubmit} className="coach-details-form">
          <div className="form-group">
            <label htmlFor="certificationType">Certification Type:</label>
            <select
              id="certificationType"
              name="certificationType"
              value={formData.certificationType}
              onChange={handleInputChange}
              className="form-control"
            >
              <option value="">Select Certification Type</option>
              <option value="Associate_Certified_Coach">
                Associate Certified Coach
              </option>
              <option value="Professional_Certified_Coach">
                Professional Certified Coach
              </option>
              <option value="Master_Certified_Coach">
                Master Certified Coach
              </option>
            </select>
          </div>
          <div className="form-group">
            <label htmlFor="yearsOfExperience">Years of Experience:</label>
            <input
              type="number"
              id="yearsOfExperience"
              name="yearsOfExperience"
              min="0"
              value={formData.yearsOfExperience}
              onChange={handleInputChange}
              className="form-control"
            />
          </div>
          <div className="form-group">
            <label htmlFor="certificateFile">Certificate:</label>
            <input
              type="file"
              id="certificateFile"
              name="certificateFile"
              onChange={handleFileChange}
              className="form-control-file"
            />
            {previewSrc && (
              <img src={previewSrc} alt="Preview" className="image-preview" />
            )}
          </div>
          <button type="submit" className="btn btn-primary">
            Submit
          </button>
        </form>
      </div>
    </div>
  );
};
