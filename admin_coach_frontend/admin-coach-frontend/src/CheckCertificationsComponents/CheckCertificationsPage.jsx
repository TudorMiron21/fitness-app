import React, { useState, useEffect } from "react";
import axios from "axios";
import { validateToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";
import { NavBar } from "../NavBarComponents/NavBar.jsx";
import { CertificationTile } from "./CertificationTile.jsx";
import "./CheckCertificationsPage.css";

export const CheckCertificationsPage = () => {
  const [certifications, setCertifications] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const checkTokenValid = async () => {
      try {
        const token = localStorage.getItem("access_token");

        if (token) {
          const isTokenValid = await validateToken(token);
          if (!isTokenValid) {
            navigate("/login");
          } else {
            await getAllNonValidatedCertifications();
          }
        } else navigate("/login");
      } catch (error) {
        console.error("Error fetching coach details status", error);
      }
    };
    checkTokenValid();
  }, []);

  const getAllNonValidatedCertifications = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/adminCoachService/admin/getAllInvalidatedCoachRequests",
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );

      const certificationsWithExpanded = response.data.map((certification) => ({
        ...certification,
        expanded: false,
      }));

      setCertifications(certificationsWithExpanded);
    } catch (err) {
      console.error("Error loading certifications", err);
    }
  };

  const handleTilePress = (certificationId) => {
    setCertifications((prevCertifications) =>
      prevCertifications.map((certification) => {
        if (certification.id === certificationId) {
          return {
            ...certification,
            expanded: !certification.expanded,
          };
        }
        return certification;
      })
    );
  };

  const validateCertification = async (certificationId) => {
    try {
      const token = localStorage.getItem("access_token");
      if (token) {
        const isTokenValid = await validateToken(token);
        if (!isTokenValid) {
          navigate("/login");
        } else {
          const response = await axios.put(
            `http://localhost:8080/api/v1/adminCoachService/admin/validateCoachRequest/${certificationId}`,
            {},
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("access_token")}`,
              },
            }
          );
          if (response.status == 200) {
            console.log(
              `certification with id ${certificationId} is validated`
            );

            setCertifications((prevCertifications) =>
              prevCertifications.filter(
                (certification) => certification.id !== certificationId // Remove the validated item
              )
            );
          } else {
            console.error("Error validating certification");
          }
        }
      } else navigate("/login");
    } catch (error) {
      console.error("Error fetching coach details status", error);
    }
  };

  return (
    <div>
      <NavBar />

      <div className="certification-grid">
        {certifications.map((certification) => (
          <CertificationTile
            key={certification.id}
            certification={certification}
            onValidate={() => validateCertification(certification.id)}
            onOpen={() => handleTilePress(certification.id)}
            onClose={() => handleTilePress(certification.id)}
          />
        ))}
      </div>
    </div>
  );
};
