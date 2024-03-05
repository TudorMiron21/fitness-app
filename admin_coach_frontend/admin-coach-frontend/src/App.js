import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import {Login} from './LoginPageComponents/LoginPage.jsx'
import { Register } from './RegisterPageComponents/RegisterPage.jsx';
import{GreetingsPage} from './GreetingsComponents/GreetingsPage.jsx'
import {CoachDetails} from './CoachDetailsComponents/CoachDetailsPage.jsx'
import{HomePage} from './HomePageComponents/HomePage.jsx'
function App() {
  return (
    <div>
      <Router>
    {/* <NavBar/> */}
        <Routes>
          <Route path="/register" element ={<Register/>}></Route>
          <Route path="/login" element ={<Login/>}></Route>
          <Route path="/coach-details" element ={<CoachDetails/>}></Route>
          <Route path="/home-page" element ={<HomePage/>}></Route>

          <Route path="/" element ={<GreetingsPage/>}></Route>

        </Routes>
      </Router>
    </div>
  );
}

export default App;
