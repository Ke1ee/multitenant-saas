import React, { useState } from 'react';
import './App.css';

const API_URL = 'http://localhost:8080';

function App() {
  const [token, setToken] = useState(null);
  const [tenantId, setTenantId] = useState(null);
  const [userEmail, setUserEmail] = useState(null);
  const [view, setView] = useState('home');
  const [projects, setProjects] = useState([]);
  const [selectedProject, setSelectedProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [message, setMessage] = useState('');

  const showMessage = (msg) => {
    setMessage(msg);
    setTimeout(() => setMessage(''), 3000);
  };

  const createTenant = async (name) => {
    const res = await fetch(`${API_URL}/api/tenants`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });
    if (res.ok) {
      const data = await res.json();
      showMessage(`Tenant "${data.name}" created!`);
      return data;
    } else {
      showMessage('Failed to create tenant');
    }
  };

  const signup = async (tenantId, email, password) => {
    const res = await fetch(`${API_URL}/api/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ tenantId, email, password })
    });
    if (res.ok) {
      const data = await res.json();
      setToken(data.token);
      setTenantId(data.tenantId);
      setUserEmail(data.email);
      setView('dashboard');
      showMessage('Signed up successfully!');
    } else {
      showMessage('Signup failed');
    }
  };

  const login = async (email, password) => {
    const res = await fetch(`${API_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    if (res.ok) {
      const data = await res.json();
      setToken(data.token);
      setTenantId(data.tenantId);
      setUserEmail(data.email);
      setView('dashboard');
      showMessage('Logged in successfully!');
    } else {
      showMessage('Login failed');
    }
  };

  const logout = () => {
    setToken(null);
    setTenantId(null);
    setUserEmail(null);
    setProjects([]);
    setSelectedProject(null);
    setTasks([]);
    setView('home');
  };

  const fetchProjects = async () => {
    const res = await fetch(`${API_URL}/api/projects`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      const data = await res.json();
      setProjects(data);
    }
  };

  const createProject = async (name, description) => {
    const res = await fetch(`${API_URL}/api/projects`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ name, description })
    });
    if (res.ok) {
      showMessage('Project created!');
      fetchProjects();
    }
  };

  const fetchTasks = async (projectId) => {
    const res = await fetch(`${API_URL}/api/projects/${projectId}/tasks`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      const data = await res.json();
      setTasks(data);
    }
  };

  const createTask = async (projectId, title, description) => {
    const res = await fetch(`${API_URL}/api/projects/${projectId}/tasks`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ title, description })
    });
    if (res.ok) {
      showMessage('Task created!');
      fetchTasks(projectId);
    }
  };

  const updateTaskStatus = async (projectId, taskId, status) => {
    const res = await fetch(`${API_URL}/api/projects/${projectId}/tasks/${taskId}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ status })
    });
    if (res.ok) {
      fetchTasks(projectId);
    }
  };

  const selectProject = (project) => {
    setSelectedProject(project);
    fetchTasks(project.id);
  };

  return (
    <div className="App">
      <header>
        <h1>Multi-Tenant SaaS Demo</h1>
        {token && (
          <div className="user-info">
            <span>{userEmail}</span>
            <button onClick={logout}>Logout</button>
          </div>
        )}
      </header>

      {message && <div className="message">{message}</div>}

      <main>
        {view === 'home' && <HomeView onNavigate={setView} />}
        {view === 'createTenant' && <CreateTenantView onCreate={createTenant} onNavigate={setView} />}
        {view === 'signup' && <SignupView onSignup={signup} onNavigate={setView} />}
        {view === 'login' && <LoginView onLogin={login} onNavigate={setView} />}
        {view === 'dashboard' && (
          <DashboardView
            projects={projects}
            selectedProject={selectedProject}
            tasks={tasks}
            onFetchProjects={fetchProjects}
            onCreateProject={createProject}
            onSelectProject={selectProject}
            onCreateTask={createTask}
            onUpdateTaskStatus={updateTaskStatus}
          />
        )}
      </main>
    </div>
  );
}

function HomeView({ onNavigate }) {
  return (
    <div className="view">
      <h2>Welcome</h2>
      <p>This is a multi-tenant SaaS demo. Each tenant's data is completely isolated.</p>
      <div className="button-group">
        <button onClick={() => onNavigate('createTenant')}>Create Tenant</button>
        <button onClick={() => onNavigate('signup')}>Sign Up</button>
        <button onClick={() => onNavigate('login')}>Login</button>
      </div>
    </div>
  );
}

function CreateTenantView({ onCreate, onNavigate }) {
  const [name, setName] = useState('');
  const [createdTenant, setCreatedTenant] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const tenant = await onCreate(name);
    if (tenant) {
      setCreatedTenant(tenant);
    }
  };

  return (
    <div className="view">
      <h2>Create Tenant</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Tenant name (e.g., Acme Corp)"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <button type="submit">Create</button>
      </form>
      {createdTenant && (
        <div className="result">
          <p>Tenant created! Save this ID for signup:</p>
          <code>{createdTenant.id}</code>
        </div>
      )}
      <button className="link-btn" onClick={() => onNavigate('home')}>Back</button>
    </div>
  );
}

function SignupView({ onSignup, onNavigate }) {
  const [tenantId, setTenantId] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSignup(tenantId, email, password);
  };

  return (
    <div className="view">
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Tenant ID"
          value={tenantId}
          onChange={(e) => setTenantId(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password (min 8 chars)"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Sign Up</button>
      </form>
      <button className="link-btn" onClick={() => onNavigate('home')}>Back</button>
    </div>
  );
}

function LoginView({ onLogin, onNavigate }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onLogin(email, password);
  };

  return (
    <div className="view">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
      <button className="link-btn" onClick={() => onNavigate('home')}>Back</button>
    </div>
  );
}

function DashboardView({ projects, selectedProject, tasks, onFetchProjects, onCreateProject, onSelectProject, onCreateTask, onUpdateTaskStatus }) {
  const [showProjectForm, setShowProjectForm] = useState(false);
  const [showTaskForm, setShowTaskForm] = useState(false);
  const [projectName, setProjectName] = useState('');
  const [projectDesc, setProjectDesc] = useState('');
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDesc, setTaskDesc] = useState('');

  React.useEffect(() => {
    onFetchProjects();
  }, []);

  const handleCreateProject = (e) => {
    e.preventDefault();
    onCreateProject(projectName, projectDesc);
    setProjectName('');
    setProjectDesc('');
    setShowProjectForm(false);
  };

  const handleCreateTask = (e) => {
    e.preventDefault();
    onCreateTask(selectedProject.id, taskTitle, taskDesc);
    setTaskTitle('');
    setTaskDesc('');
    setShowTaskForm(false);
  };

  return (
    <div className="dashboard">
      <div className="sidebar">
        <div className="sidebar-header">
          <h3>Projects</h3>
          <button onClick={() => setShowProjectForm(!showProjectForm)}>+</button>
        </div>
        {showProjectForm && (
          <form onSubmit={handleCreateProject} className="inline-form">
            <input
              type="text"
              placeholder="Project name"
              value={projectName}
              onChange={(e) => setProjectName(e.target.value)}
              required
            />
            <input
              type="text"
              placeholder="Description"
              value={projectDesc}
              onChange={(e) => setProjectDesc(e.target.value)}
            />
            <button type="submit">Create</button>
          </form>
        )}
        <ul className="project-list">
          {projects.map((p) => (
            <li
              key={p.id}
              className={selectedProject?.id === p.id ? 'selected' : ''}
              onClick={() => onSelectProject(p)}
            >
              {p.name}
            </li>
          ))}
        </ul>
      </div>

      <div className="main-content">
        {selectedProject ? (
          <>
            <div className="content-header">
              <h2>{selectedProject.name}</h2>
              <button onClick={() => setShowTaskForm(!showTaskForm)}>+ Add Task</button>
            </div>
            <p className="project-desc">{selectedProject.description}</p>

            {showTaskForm && (
              <form onSubmit={handleCreateTask} className="inline-form">
                <input
                  type="text"
                  placeholder="Task title"
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  required
                />
                <input
                  type="text"
                  placeholder="Description"
                  value={taskDesc}
                  onChange={(e) => setTaskDesc(e.target.value)}
                />
                <button type="submit">Create</button>
              </form>
            )}

            <div className="tasks">
              {tasks.map((task) => (
                <div key={task.id} className={`task ${task.status.toLowerCase()}`}>
                  <div className="task-info">
                    <strong>{task.title}</strong>
                    <p>{task.description}</p>
                  </div>
                  <select
                    value={task.status}
                    onChange={(e) => onUpdateTaskStatus(selectedProject.id, task.id, e.target.value)}
                  >
                    <option value="TODO">TODO</option>
                    <option value="IN_PROGRESS">IN PROGRESS</option>
                    <option value="DONE">DONE</option>
                  </select>
                </div>
              ))}
            </div>
          </>
        ) : (
          <p className="no-selection">Select a project to view tasks</p>
        )}
      </div>
    </div>
  );
}

export default App;