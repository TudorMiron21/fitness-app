import React from "react";
import { Card, CardMedia, CardContent, Typography } from "@mui/material";

export const CertificationTile = ({ certification }) => {
  const getStatus = () => {
    if (certification.isValidated === null) {
      return "Status: Pending";
    } else if (certification.isValidated) {
      return "Status: Verified";
    } else {
      return "Status: Denied";
    }
  };

  return (
    <Card sx={{ maxWidth: 345, margin: 2 }}>
      {/* Cover Photo */}
      <CardMedia
        component="img"
        height="300"
        image={
          certification.imageResourcePreSignedUrl ||
          "https://via.placeholder.com/140x140"
        }
        alt="certification picture"
      />

      <CardContent>
        <Typography variant="h5" component="div">
          {certification.certificationType}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Years of Experience: {certification.yearsOfExperience || "Unknown"}
        </Typography>

        <Typography variant="body2" color="text.secondary">
          {getStatus()}
        </Typography>
      </CardContent>
    </Card>
  );
};
