import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import {Login} from './LoginPageComponents/LoginPage.jsx'

function App() {
  return (
    <div>
      <Router>
    {/* <NavBar/> */}
        <Routes>
          {/* <Route path="/register" element ={<Register/>}></Route> */}
          <Route path="/login" element ={<Login/>}></Route>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
