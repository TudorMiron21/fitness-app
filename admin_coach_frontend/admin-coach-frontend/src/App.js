import "./App.css";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { Login } from "./LoginPageComponents/LoginPage.jsx";
import { Register } from "./RegisterPageComponents/RegisterPage.jsx";
import { GreetingsPage } from "./GreetingsComponents/GreetingsPage.jsx";
import { CoachDetails } from "./CoachDetailsComponents/CoachDetailsPage.jsx";
import { HomePage } from "./HomePageComponents/HomePage.jsx";
import { CreateExercise } from "./CreateExerciseComponents/CreateExercisePage.jsx";
import { CreateWorkoutPage } from "./CreateWorkoutComponents/CreateWorkoutPage.jsx";
import { CreateProgramPage } from "./CreateProgramComponents/CreateProgramPage.jsx";
import { ChatPage } from "./ChatComponent/ChatPage.jsx";
import { CheckCertificationsPage } from "./CheckCertificationsComponents/CheckCertificationsPage.jsx";
function App() {
  return (
    <div>
      <Router>
        {/* <NavBar/> */}
        <Routes>
          <Route path="/register" element={<Register />}></Route>
          <Route path="/login" element={<Login />}></Route>
          <Route path="/coach-details" element={<CoachDetails />}></Route>
          <Route path="/home-page" element={<HomePage />}></Route>
          <Route
            path="/create-exercise-page"
            element={<CreateExercise />}
          ></Route>

          <Route
            path="/create-workout-page"
            element={<CreateWorkoutPage />}
          ></Route>

          <Route
            path="/create-program-page"
            element={<CreateProgramPage />}
          ></Route>
          <Route path="/" element={<GreetingsPage />}></Route>
          <Route path="/chat-page" element={<ChatPage />}></Route>
          <Route
            path="/certifications"
            element={<CheckCertificationsPage />}
          ></Route>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
