import React, { useState, useEffect, useRef } from "react";
import "./CertificationTile.css"; // Custom CSS for styling

export const CertificationTile = ({
  certification,
  onValidate,
  onOpen,
  onClose,
}) => {
  const {
    imageResourcePreSignedUrl,
    user,
    certificationType,
    yearsOfExperience,
  } = certification;

  const [expanded, setExpanded] = useState(false);
  const [scale, setScale] = useState(1);
  const [position, setPosition] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const [startDrag, setStartDrag] = useState({ x: 0, y: 0 });

  const imageRef = useRef(null);

  // Function to toggle expanded state and reset zoom/position
  const handlePress = () => {
    setExpanded(true);
    setScale(1); // Reset zoom when toggling expanded state
    setPosition({ x: 0, y: 0 });
    onOpen(certification.id);
  };

  // Mouse wheel handler for zooming
  const handleWheel = (event) => {
    event.preventDefault(); // Prevent page scroll
    const delta = event.deltaY > 0 ? -0.1 : 0.1; // Zoom direction
    const newScale = Math.max(0.5, Math.min(3, scale + delta)); // Constrain zoom
    setScale(newScale);
  };

  // Mouse down handler for starting drag (panning)
  const handleMouseDown = (event) => {
    setIsDragging(true);
    setStartDrag({ x: event.clientX, y: event.clientY });
  };

  // Mouse move handler for panning
  const handleMouseMove = (event) => {
    if (isDragging) {
      const dx = event.clientX - startDrag.x;
      const dy = event.clientY - startDrag.y;
      setPosition((pos) => ({
        x: pos.x + dx,
        y: pos.y + dy,
      }));
      setStartDrag({ x: event.clientX, y: event.clientY }); // Reset start position
    }
  };

  // Mouse up handler to stop dragging
  const handleMouseUp = () => {
    setIsDragging(false);
  };

  useEffect(() => {
    const img = imageRef.current;
    if (img) {
      img.addEventListener("wheel", handleWheel); // Zooming
      img.addEventListener("mousedown", handleMouseDown); // Start dragging
      document.addEventListener("mousemove", handleMouseMove); // During dragging
      document.addEventListener("mouseup", handleMouseUp); // Stop dragging

      return () => {
        img.removeEventListener("wheel", handleWheel);
        img.removeEventListener("mousedown", handleMouseDown);
        document.removeEventListener("mousemove", handleMouseMove);
        document.removeEventListener("mouseup", handleMouseUp);
      };
    }
  }, [scale, position]); // React to scale and position changes

  return (
    <div
      className={`certification-tile ${expanded ? "expanded" : ""}`}
      onClick={handlePress}
    >
      <div
        style={{
          overflow: expanded ? "auto" : "hidden", // Scrollable when expanded
          width: expanded ? "100%" : "200px",
          height: expanded ? "100%" : "200px",
          cursor: isDragging ? "grabbing" : "grab", // Change cursor during drag
        }}
      >
        <img
          ref={imageRef}
          className="certification-image"
          src={imageResourcePreSignedUrl}
          alt="Certification"
          style={{
            transform: `scale(${scale}) translate(${position.x}px, ${position.y}px)`, // Apply zoom and pan
            transition: isDragging ? "none" : "transform 0.2s", // Smooth transition
            objectfit: expanded ? "contain" : "cover", // Adjust object-fit
          }}
        />
      </div>
      <h3 className="certification-type">{certificationType}</h3>
      <p className="years-of-experience">
        Years of Experience: {yearsOfExperience}
      </p>
      <p className="coach-name">Coach: {user.email}</p>
      {expanded && (
        <div className="expanded-content">
          <button className="validate-button" onClick={onValidate}>
            Validate
          </button>
          <button
            className="close-button"
            onClick={(event) => {
              event.stopPropagation();
              setExpanded(false);
              setScale(1); // Reset scale on close
              setPosition({ x: 0, y: 0 });
              onClose(certification.id);
            }}
          >
            Close
          </button>
        </div>
      )}
    </div>
  );
};
