import React, { useEffect, useState } from "react";
import { Grid, CircularProgress } from "@mui/material";
import axios from "axios";
import { CertificationTile } from "./CertificationTile";

export const CertificationGrid = () => {
  const [certifications, setCertifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCertifications = async () => {
      try {
        const response = await axios.get(
          "https://fit-stack.online/api/v1/adminCoachService/coach/getAllCertificationsForCoach",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("access_token")}`,
            },
          }
        );
        setCertifications(response.data);
      } catch (error) {
        console.error("Error fetching certifications:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchCertifications();
  }, []);

  return (
    <div>
      {loading ? (
        <CircularProgress />
      ) : (
        <Grid container spacing={2} justifyContent="center">
          {certifications.map((certification, index) => (
            <Grid item key={index}>
              <CertificationTile certification={certification} />
            </Grid>
          ))}
        </Grid>
      )}
    </div>
  );
};
