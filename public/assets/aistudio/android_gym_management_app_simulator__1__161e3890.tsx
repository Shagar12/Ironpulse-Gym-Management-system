import React, { useState, useEffect, useMemo } from 'react';
import { initializeApp } from 'firebase/app';
import { 
  getAuth, 
  signInAnonymously, 
  onAuthStateChanged 
} from 'firebase/auth';
import { 
  getFirestore, 
  doc, 
  setDoc, 
  collection, 
  onSnapshot 
} from 'firebase/firestore';
import { 
  Dumbbell, 
  Utensils, 
  TrendingUp, 
  Users, 
  User, 
  LogOut, 
  Plus, 
  Trash2, 
  Activity, 
  Flame, 
  Scale, 
  ShieldCheck, 
  ChevronRight,
  Search,
  RefreshCw,
  Check,
  Zap,
  Lock,
  Mail,
  PieChart,
  Smartphone,
  Wifi,
  Battery,
  ChevronLeft,
  Settings,
  Bell,
  Sliders,
  Award,
  Clock,
  CheckCircle2,
  XCircle,
  FileText,
  BarChart2,
  MapPin
} from 'lucide-react';

// ==========================================
// FIREBASE SETUP & INITIALIZATION
// ==========================================
let firebaseApp = null;
let auth = null;
let db = null;
let appId = 'gym-android-app-v2';

try {
  const firebaseConfig = typeof __firebase_config !== 'undefined' 
    ? JSON.parse(__firebase_config) 
    : null;
  
  if (firebaseConfig) {
    firebaseApp = initializeApp(firebaseConfig);
    auth = getAuth(firebaseApp);
    db = getFirestore(firebaseApp);
  }
  if (typeof __app_id !== 'undefined') {
    appId = __app_id;
  }
} catch (err) {
  console.warn("Firebase config not available. Operating in local simulated state sync mode.", err);
}

// Initial Seed Data for Android Simulator
const INITIAL_CLIENTS = [
  {
    id: 'client_alex_01',
    email: 'client@fitgym.com',
    name: 'Alex Rivera',
    gender: 'Male',
    age: 26,
    goalPhase: 'Cutting', // Cutting | Maintaining | Lean Bulking | Bulking
    challengeLevel: 'Intermediate', // Beginner | Intermediate | Advanced | Elite
    targetWeight: 75,
    currentWeight: 81,
    heightCm: 178,
    dailyCalories: 2100,
    proteinG: 180,
    carbsG: 190,
    fatG: 60,
    approved: true,
    atGym: false,
    subscriptionDaysLeft: 18,
    dietPlan: [
      { id: 'm1', name: 'Meal 1: Power Breakfast', desc: '4 egg whites, 1 whole egg, 60g oats with berries', time: '08:00 AM', completed: true },
      { id: 'm2', name: 'Meal 2: Whey Isolate Shake', desc: '1 scoop Whey, 1 banana, almond milk', time: '11:30 AM', completed: true },
      { id: 'm3', name: 'Meal 3: Lean Lunch', desc: '180g grilled chicken, 150g jasmine rice', time: '02:30 PM', completed: false },
      { id: 'm4', name: 'Meal 4: High-Protein Dinner', desc: '200g salmon fillet, mixed salad, olive oil', time: '07:30 PM', completed: false }
    ],
    workoutPlan: [
      { id: 'w1', exercise: 'Incline Barbell Press', sets: '4', reps: '8-10', weight: '70kg', completed: true },
      { id: 'w2', exercise: 'Dumbbell Incline Flyes', sets: '3', reps: '12-15', weight: '18kg', completed: true },
      { id: 'w3', exercise: 'Weighted Tricep Dips', sets: '4', reps: '10', weight: '15kg', completed: false },
      { id: 'w4', exercise: 'Cable Rope Pushdowns', sets: '3', reps: '15', weight: '25kg', completed: false }
    ],
    weightHistory: [
      { date: '10/01', weight: 85.0 },
      { date: '10/12', weight: 83.5 },
      { date: '10/24', weight: 82.1 },
      { date: '11/03', weight: 81.0 }
    ]
  },
  {
    id: 'client_sarah_02',
    email: 'sarah@fitgym.com',
    name: 'Sarah Jenkins',
    gender: 'Female',
    age: 24,
    goalPhase: 'Lean Bulking',
    challengeLevel: 'Advanced',
    targetWeight: 62,
    currentWeight: 56,
    heightCm: 165,
    dailyCalories: 2400,
    proteinG: 140,
    carbsG: 280,
    fatG: 70,
    approved: true,
    atGym: true,
    subscriptionDaysLeft: 3, // Due soon warning
    dietPlan: [
      { id: 'm1', name: 'Meal 1: Oats & Peanut Butter', desc: '80g oats, 2 tbsp PB, chia seeds', time: '08:30 AM', completed: true },
      { id: 'm2', name: 'Meal 2: Chicken Wrap', desc: 'Whole wheat wrap, 150g chicken breast', time: '01:00 PM', completed: true }
    ],
    workoutPlan: [
      { id: 'w1', exercise: 'Barbell Back Squat', sets: '4', reps: '6-8', weight: '65kg', completed: true },
      { id: 'w2', exercise: 'Romanian Deadlifts', sets: '4', reps: '8-10', weight: '70kg', completed: true }
    ],
    weightHistory: [
      { date: '10/01', weight: 54.5 },
      { date: '10/15', weight: 55.2 },
      { date: '11/02', weight: 56.0 }
    ]
  },
  {
    id: 'client_mike_03',
    email: 'newbie@fitgym.com',
    name: 'Mike Ross',
    gender: 'Male',
    age: 30,
    goalPhase: 'Cutting',
    challengeLevel: 'Beginner',
    targetWeight: 80,
    currentWeight: 92,
    heightCm: 180,
    dailyCalories: 2200,
    proteinG: 170,
    carbsG: 180,
    fatG: 65,
    approved: false, // Pending coach approval
    atGym: false,
    subscriptionDaysLeft: 30,
    dietPlan: [
      { id: 'm1', name: 'Egg White Omelet', desc: '5 whites, spinach, tomatoes', time: '09:00 AM', completed: false }
    ],
    workoutPlan: [
      { id: 'w1', exercise: 'Bodyweight Pushups', sets: '3', reps: '10', weight: 'Bodyweight', completed: false }
    ],
    weightHistory: [
      { date: '11/01', weight: 92.0 }
    ]
  }
];

const INITIAL_AUDIT_LOGS = [
  { id: 'log_1', time: '10:14 AM', text: 'System initialized with 3 client profiles.', type: 'system' },
  { id: 'log_2', time: '11:30 AM', text: 'Coach updated Alex Rivera goal phase to Cutting.', type: 'coach' },
  { id: 'log_3', time: '12:05 PM', text: 'Sarah Jenkins checked in ("I am at the gym").', type: 'gym' },
  { id: 'log_4', time: '01:20 PM', text: 'New client registration: Mike Ross (Pending Approval).', type: 'approval' }
];

export default function App() {
  const [userRole, setUserRole] = useState(null); // 'admin' | 'client' | null
  const [authEmail, setAuthEmail] = useState('');
  const [authPassword, setAuthPassword] = useState('');
  const [clients, setClients] = useState(INITIAL_CLIENTS);
  const [auditLogs, setAuditLogs] = useState(INITIAL_AUDIT_LOGS);
  const [selectedClientId, setSelectedClientId] = useState('client_alex_01');
  const [activeTab, setActiveTab] = useState('roster'); // roster | diet | workout | audit | billing for coach; home | diet | workout | analytics for client
  const [analyticsView, setAnalyticsView] = useState('bar'); // 'bar' | 'pie'

  // Modal / Form States
  const [isAddingClientModal, setIsAddingClientModal] = useState(false);
  const [isAiModalOpen, setIsAiModalOpen] = useState(false);
  const [newWeightInput, setNewWeightInput] = useState('');
  
  // New Client Form State
  const [newClientForm, setNewClientForm] = useState({
    name: '', email: '', gender: 'Male', age: 25, goalPhase: 'Maintaining', challengeLevel: 'Beginner', currentWeight: 75, targetWeight: 72, heightCm: 175
  });

  // AI Assistant Generator Form State
  const [aiForm, setAiForm] = useState({
    age: 25, gender: 'Male', weight: 78, height: 175, goalPhase: 'Cutting', challengeLevel: 'Intermediate'
  });

  // Live Android Clock State
  const [currentTime, setCurrentTime] = useState('10:42 AM');
  useEffect(() => {
    const timer = setInterval(() => {
      const now = new Date();
      setCurrentTime(now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // Active client object for client portal or selected coach view
  const activeClient = useMemo(() => {
    if (userRole === 'client') {
      return clients.find(c => c.email === authEmail) || clients[0];
    }
    return clients.find(c => c.id === selectedClientId) || clients[0];
  }, [clients, selectedClientId, userRole, authEmail]);

  // Add Audit Log Helper
  const addAuditLog = (text, type = 'coach') => {
    const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    setAuditLogs(prev => [{ id: `log_${Date.now()}`, time: timeStr, text, type }, ...prev]);
  };

  // Login Handler
  const handleLogin = (e, forcedRole = null, forcedEmail = null) => {
    if (e) e.preventDefault();
    const role = forcedRole || (authEmail.includes('coach') || authEmail.includes('admin') ? 'admin' : 'client');
    const email = forcedEmail || authEmail || (role === 'admin' ? 'coach@fitgym.com' : 'client@fitgym.com');

    setUserRole(role);
    setAuthEmail(email);
    if (role === 'admin') {
      setActiveTab('roster');
    } else {
      setActiveTab('home');
    }
    addAuditLog(`User logged in as ${role.toUpperCase()} (${email})`, 'system');
  };

  const handleLogout = () => {
    setUserRole(null);
    setAuthEmail('');
    setAuthPassword('');
  };

  // Save Client Data Helper
  const saveClientData = (updatedClient) => {
    setClients(prev => prev.map(c => c.id === updatedClient.id ? updatedClient : c));
  };

  // Helper for BMI calculation
  const calculateBMI = (weightKg, heightCm) => {
    if (!weightKg || !heightCm) return { bmi: 0, category: 'Unknown', color: 'text-slate-400', bg: 'bg-slate-800' };
    const heightM = heightCm / 100;
    const bmiVal = (weightKg / (heightM * heightM)).toFixed(1);
    let category = 'Normal';
    let color = 'text-emerald-400';
    let bg = 'bg-emerald-500/20 border-emerald-500/30';

    if (bmiVal < 18.5) {
      category = 'Underweight';
      color = 'text-cyan-400';
      bg = 'bg-cyan-500/20 border-cyan-500/30';
    } else if (bmiVal >= 25 && bmiVal < 30) {
      category = 'Overweight';
      color = 'text-amber-400';
      bg = 'bg-amber-500/20 border-amber-500/30';
    } else if (bmiVal >= 30) {
      category = 'Obese';
      color = 'text-rose-400';
      bg = 'bg-rose-500/20 border-rose-500/30';
    }
    return { bmi: bmiVal, category, color, bg };
  };

  // Phase Macro Adjuster
  const handleUpdatePhase = (phase) => {
    let cal = activeClient.dailyCalories;
    let prot = activeClient.proteinG;
    let carb = activeClient.carbsG;
    let fat = activeClient.fatG;
    const bw = activeClient.currentWeight || 75;

    if (phase === 'Cutting') {
      cal = Math.round(bw * 28);
      prot = Math.round(bw * 2.2);
      fat = Math.round(bw * 0.7);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    } else if (phase === 'Lean Bulking') {
      cal = Math.round(bw * 35); // Moderate surplus (~+250 kcal)
      prot = Math.round(bw * 2.2);
      fat = Math.round(bw * 0.85);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    } else if (phase === 'Bulking') {
      cal = Math.round(bw * 38); // Aggressive surplus (~+500 kcal)
      prot = Math.round(bw * 2.0);
      fat = Math.round(bw * 1.0);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    } else {
      cal = Math.round(bw * 33);
      prot = Math.round(bw * 2.0);
      fat = Math.round(bw * 0.9);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    }

    saveClientData({
      ...activeClient,
      goalPhase: phase,
      dailyCalories: cal,
      proteinG: prot,
      carbsG: carb,
      fatG: fat
    });
    addAuditLog(`Changed phase for ${activeClient.name} to ${phase}`, 'coach');
  };

  const handleUpdateChallengeLevel = (level) => {
    saveClientData({
      ...activeClient,
      challengeLevel: level
    });
    addAuditLog(`Set challenge level for ${activeClient.name} to ${level}`, 'coach');
  };

  const toggleMeal = (id) => {
    const updated = activeClient.dietPlan.map(m => m.id === id ? { ...m, completed: !m.completed } : m);
    saveClientData({ ...activeClient, dietPlan: updated });
  };

  const toggleWorkout = (id) => {
    const updated = activeClient.workoutPlan.map(w => w.id === id ? { ...w, completed: !w.completed } : w);
    saveClientData({ ...activeClient, workoutPlan: updated });
  };

  const handleAddWeightLog = (e) => {
    e.preventDefault();
    if (!newWeightInput || isNaN(newWeightInput)) return;
    const w = parseFloat(newWeightInput);
    const today = new Date().toLocaleDateString([], { month: '2-digit', day: '2-digit' });
    saveClientData({
      ...activeClient,
      currentWeight: w,
      weightHistory: [...(activeClient.weightHistory || []), { date: today, weight: w }]
    });
    setNewWeightInput('');
    addAuditLog(`${activeClient.name} logged new weight: ${w}kg`, 'gym');
  };

  const handleAddMeal = (mealObj) => {
    const updated = [...(activeClient.dietPlan || []), { id: `m_${Date.now()}`, ...mealObj, completed: false }];
    saveClientData({ ...activeClient, dietPlan: updated });
  };

  const handleAddWorkout = (workoutObj) => {
    const updated = [...(activeClient.workoutPlan || []), { id: `w_${Date.now()}`, ...workoutObj, completed: false }];
    saveClientData({ ...activeClient, workoutPlan: updated });
  };

  // Gym Check-In Toggle for Client
  const toggleGymCheckIn = () => {
    const newState = !activeClient.atGym;
    saveClientData({ ...activeClient, atGym: newState });
    if (newState) {
      addAuditLog(`${activeClient.name} checked in: "I am at the gym" 📍`, 'gym');
    } else {
      addAuditLog(`${activeClient.name} checked out from the gym.`, 'gym');
    }
  };

  // Client Approval Action by Coach
  const setClientApproval = (clientId, status) => {
    setClients(prev => prev.map(c => c.id === clientId ? { ...c, approved: status } : c));
    const target = clients.find(c => c.id === clientId);
    addAuditLog(`Coach ${status ? 'APPROVED' : 'DECLINED'} membership for ${target?.name || clientId}`, 'approval');
  };

  // Renew 30-Day Subscription Fee
  const renewSubscription = (clientId) => {
    setClients(prev => prev.map(c => c.id === clientId ? { ...c, subscriptionDaysLeft: 30 } : c));
    const target = clients.find(c => c.id === clientId);
    addAuditLog(`Processed 30-day subscription fee payment for ${target?.name}`, 'billing');
  };

  // AI Generator Execution
  const runAiPlanGenerator = () => {
    const bw = Number(aiForm.weight) || 75;
    const phase = aiForm.goalPhase;
    let cal = 2200;
    let prot = Math.round(bw * 2.2);
    let fat = Math.round(bw * 0.8);
    let carb = 200;

    if (phase === 'Cutting') {
      cal = Math.round(bw * 27);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    } else if (phase === 'Lean Bulking') {
      cal = Math.round(bw * 34);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    } else if (phase === 'Bulking') {
      cal = Math.round(bw * 38);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    } else {
      cal = Math.round(bw * 31);
      carb = Math.round((cal - (prot * 4 + fat * 9)) / 4);
    }

    const aiDiet = [
      { id: 'ai_1', name: `AI ${phase} Breakfast`, desc: `${aiForm.gender === 'Female' ? '3' : '4'} egg whites, oats, berries`, time: '08:00 AM', completed: false },
      { id: 'ai_2', name: 'AI Midday Protein', desc: 'Whey isolate shake & handful of almonds', time: '12:00 PM', completed: false },
      { id: 'ai_3', name: 'AI Post-Workout Meal', desc: '180g lean meat / fish with jasmine rice', time: '03:30 PM', completed: false }
    ];

    const aiWorkout = [
      { id: 'aw_1', exercise: aiForm.challengeLevel === 'Beginner' ? 'Dumbbell Goblet Squat' : 'Barbell Back Squat', sets: '4', reps: '8-10', weight: 'Standard', completed: false },
      { id: 'aw_2', exercise: 'Incline Bench Press', sets: '4', reps: '10', weight: 'Moderate', completed: false },
      { id: 'aw_3', exercise: 'Lat Pulldowns', sets: '3', reps: '12', weight: 'Moderate', completed: false }
    ];

    saveClientData({
      ...activeClient,
      currentWeight: bw,
      heightCm: Number(aiForm.height) || activeClient.heightCm,
      gender: aiForm.gender,
      age: Number(aiForm.age) || activeClient.age,
      goalPhase: phase,
      challengeLevel: aiForm.challengeLevel,
      dailyCalories: cal,
      proteinG: prot,
      carbsG: carb,
      fatG: fat,
      dietPlan: aiDiet,
      workoutPlan: aiWorkout
    });

    setIsAiModalOpen(false);
    addAuditLog(`AI Assistant generated custom ${phase} routine for ${activeClient.name}`, 'coach');
  };

  const handleAddNewClientSubmit = (e) => {
    e.preventDefault();
    if (!newClientForm.name || !newClientForm.email) return;
    const newId = `client_${Date.now()}`;
    const newClientObj = {
      id: newId,
      ...newClientForm,
      approved: true,
      atGym: false,
      subscriptionDaysLeft: 30,
      dietPlan: [
        { id: 'm1', name: 'Default Protein Breakfast', desc: 'Oats and eggs', time: '08:00 AM', completed: false }
      ],
      workoutPlan: [
        { id: 'w1', exercise: 'Full Body Compound Circuit', sets: '3', reps: '12', weight: 'Bodyweight', completed: false }
      ],
      weightHistory: [
        { date: '11/03', weight: Number(newClientForm.currentWeight) || 75 }
      ]
    };
    setClients(prev => [...prev, newClientObj]);
    setSelectedClientId(newId);
    setIsAddingClientModal(false);
    setNewClientForm({ name: '', email: '', gender: 'Male', age: 25, goalPhase: 'Maintaining', challengeLevel: 'Beginner', currentWeight: 75, targetWeight: 72, heightCm: 175 });
    addAuditLog(`Coach created new client profile for ${newClientObj.name}`, 'coach');
  };

  const completedMeals = activeClient?.dietPlan?.filter(m => m.completed).length || 0;
  const totalMeals = activeClient?.dietPlan?.length || 1;
  const mealPct = Math.round((completedMeals / totalMeals) * 100);

  const completedWorkouts = activeClient?.workoutPlan?.filter(w => w.completed).length || 0;
  const totalWorkouts = activeClient?.workoutPlan?.length || 1;
  const workoutPct = Math.round((completedWorkouts / totalWorkouts) * 100);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-2 sm:p-6 font-sans">
      {/* Simulation Header Title */}
      <div className="mb-4 text-center">
        <h1 className="text-xl font-extrabold bg-gradient-to-r from-emerald-400 via-cyan-400 to-indigo-400 bg-clip-text text-transparent">
          IRON PULSE Android Edition
        </h1>
        <p className="text-xs text-slate-400 mt-0.5">Firebase Synced • Dual Portal Gym OS • AI Assistant & Billing</p>
      </div>

      {/* ANDROID PHONE FRAME */}
      <div className="w-full max-w-[390px] h-[800px] bg-slate-900 rounded-[48px] border-[10px] border-slate-800 shadow-2xl relative overflow-hidden flex flex-col ring-1 ring-slate-700/50">
        
        {/* ANDROID TOP STATUS BAR */}
        <div className="h-9 bg-slate-950 px-6 flex items-center justify-between text-xs text-slate-300 font-medium select-none z-30 shrink-0 border-b border-slate-800/40">
          <span>{currentTime}</span>
          {/* Camera Notch */}
          <div className="w-20 h-4 bg-slate-900 rounded-full flex items-center justify-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-slate-950 border border-slate-800" />
            <div className="w-1.5 h-1.5 rounded-full bg-cyan-900/40" />
          </div>
          <div className="flex items-center gap-2 text-[10px]">
            <Wifi className="w-3.5 h-3.5 text-slate-300" />
            <Battery className="w-4 h-4 text-emerald-400 fill-emerald-400" />
          </div>
        </div>

        {/* APP CONTENT AREA */}
        <div className="flex-1 bg-slate-950 overflow-y-auto overflow-x-hidden relative flex flex-col">
          
          {/* ========================================== */}
          {/* 1. AUTH / LOGIN SCREEN                     */}
          {/* ========================================== */}
          {!userRole ? (
            <div className="flex-1 p-6 flex flex-col justify-between">
              <div className="mt-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-tr from-emerald-400 to-cyan-500 rounded-2xl flex items-center justify-center mx-auto shadow-lg shadow-emerald-500/20 mb-4">
                  <Dumbbell className="w-9 h-9 text-slate-950 transform -rotate-45" />
                </div>
                <h2 className="text-2xl font-black text-slate-100 tracking-wide">IRON PULSE</h2>
                <p className="text-xs text-cyan-400 font-semibold uppercase tracking-widest mt-0.5">Android Gym Portal OS</p>
              </div>

              {/* Demo Fast Logins */}
              <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl space-y-2">
                <p className="text-[11px] font-bold text-slate-400 uppercase text-center tracking-wider">Tap One-Click Demo Access</p>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    onClick={() => handleLogin(null, 'admin', 'coach@fitgym.com')}
                    className="flex flex-col items-center justify-center p-2.5 bg-cyan-500/10 hover:bg-cyan-500/20 border border-cyan-500/30 rounded-xl transition"
                  >
                    <ShieldCheck className="w-5 h-5 text-cyan-400 mb-1" />
                    <span className="text-xs font-bold text-cyan-400">Coach Portal</span>
                  </button>
                  <button
                    onClick={() => handleLogin(null, 'client', 'client@fitgym.com')}
                    className="flex flex-col items-center justify-center p-2.5 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/30 rounded-xl transition"
                  >
                    <User className="w-5 h-5 text-emerald-400 mb-1" />
                    <span className="text-xs font-bold text-emerald-400">Client Portal</span>
                  </button>
                </div>
              </div>

              {/* Login Form */}
              <form onSubmit={handleLogin} className="space-y-3">
                <div>
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Email Address</label>
                  <div className="relative">
                    <Mail className="w-4 h-4 text-slate-500 absolute left-3 top-1/2 -translate-y-1/2" />
                    <input
                      type="email"
                      required
                      value={authEmail}
                      onChange={(e) => setAuthEmail(e.target.value)}
                      placeholder="coach@fitgym.com or client@fitgym.com"
                      className="w-full bg-slate-900 border border-slate-800 rounded-xl pl-9 pr-3 py-2.5 text-xs text-slate-200 placeholder-slate-600 focus:outline-none focus:border-cyan-500"
                    />
                  </div>
                </div>

                <div>
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block mb-1">Password</label>
                  <div className="relative">
                    <Lock className="w-4 h-4 text-slate-500 absolute left-3 top-1/2 -translate-y-1/2" />
                    <input
                      type="password"
                      required
                      value={authPassword}
                      onChange={(e) => setAuthPassword(e.target.value)}
                      placeholder="••••••••"
                      className="w-full bg-slate-900 border border-slate-800 rounded-xl pl-9 pr-3 py-2.5 text-xs text-slate-200 placeholder-slate-600 focus:outline-none focus:border-cyan-500"
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  className="w-full bg-gradient-to-r from-emerald-400 to-cyan-500 text-slate-950 font-black py-3 rounded-xl text-xs uppercase tracking-wider shadow-lg shadow-emerald-500/20 active:scale-98 transition"
                >
                  Sign In To App
                </button>
              </form>

              <div className="text-center pb-2">
                <p className="text-[10px] text-slate-500">Android SDK • Firebase Realtime Sync</p>
              </div>
            </div>
          ) : (
            <>
              {/* APP HEADER */}
              <div className="bg-slate-900/90 border-b border-slate-800 px-4 py-3 flex items-center justify-between sticky top-0 z-20 backdrop-blur-md">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 bg-gradient-to-tr from-emerald-400 to-cyan-500 rounded-lg flex items-center justify-center">
                    <Dumbbell className="w-4 h-4 text-slate-950 transform -rotate-45" />
                  </div>
                  <div>
                    <h3 className="text-xs font-black text-slate-100 leading-none">IRON PULSE</h3>
                    <span className="text-[9px] font-extrabold text-cyan-400 uppercase tracking-wider">
                      {userRole === 'admin' ? 'Coach Portal' : activeClient?.name}
                    </span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <button onClick={handleLogout} className="p-1.5 bg-slate-950 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 rounded-lg border border-slate-800 transition">
                    <LogOut className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>

              {/* CLIENT SUBSCRIPTION LOCK OVERLAY (If subscription expired) */}
              {userRole === 'client' && activeClient && activeClient.subscriptionDaysLeft <= 0 ? (
                <div className="flex-1 p-6 flex flex-col items-center justify-center text-center space-y-4">
                  <div className="w-16 h-16 bg-rose-500/20 border border-rose-500/30 rounded-2xl flex items-center justify-center text-rose-400 shadow-xl">
                    <Lock className="w-8 h-8" />
                  </div>
                  <div>
                    <h3 className="text-base font-black text-slate-100">Subscription Expired</h3>
                    <p className="text-xs text-slate-400 mt-1">Your 30-day gym membership fee is due. Please settle your account with the coach to restore app access.</p>
                  </div>
                  <button
                    onClick={() => renewSubscription(activeClient.id)}
                    className="w-full bg-gradient-to-r from-emerald-400 to-cyan-500 text-slate-950 font-black py-3 rounded-xl text-xs uppercase tracking-wider shadow-lg"
                  >
                    Pay 30-Day Fee Now ($49.00)
                  </button>
                  <button onClick={handleLogout} className="text-xs text-slate-500 underline">Sign Out</button>
                </div>
              ) : userRole === 'client' && activeClient && !activeClient.approved ? (
                /* PENDING APPROVAL SCREEN */
                <div className="flex-1 p-6 flex flex-col items-center justify-center text-center space-y-4">
                  <div className="w-16 h-16 bg-amber-500/20 border border-amber-500/30 rounded-2xl flex items-center justify-center text-amber-400 shadow-xl animate-pulse">
                    <Clock className="w-8 h-8" />
                  </div>
                  <div>
                    <h3 className="text-base font-black text-slate-100">Approval Pending</h3>
                    <p className="text-xs text-slate-400 mt-1">Your account registration is awaiting review and verification by your gym coach.</p>
                  </div>
                  <button onClick={handleLogout} className="text-xs text-cyan-400 underline font-bold">Sign Out or Switch Account</button>
                </div>
              ) : (
                /* NORMAL PORTAL CONTENT */
                <div className="flex-1 p-4 space-y-4 pb-16">
                  
                  {/* ========================================== */}
                  {/* 2. COACH ADMIN PORTAL VIEWS                 */}
                  {/* ========================================== */}
                  {userRole === 'admin' && (
                    <>
                      {/* COACH: ROSTER TAB */}
                      {activeTab === 'roster' && (
                        <div className="space-y-4">
                          <div className="flex justify-between items-center">
                            <h4 className="text-sm font-extrabold text-slate-100 flex items-center gap-1.5">
                              <Users className="w-4 h-4 text-cyan-400" /> Client Roster ({clients.length})
                            </h4>
                            <div className="flex gap-1">
                              <button
                                onClick={() => setIsAiModalOpen(true)}
                                className="bg-indigo-500 text-slate-100 px-2.5 py-1 rounded-lg text-[10px] font-black uppercase flex items-center gap-1 shadow-md shadow-indigo-500/20"
                              >
                                <Zap className="w-3 h-3" /> AI Gen
                              </button>
                              <button
                                onClick={() => setIsAddingClientModal(true)}
                                className="bg-cyan-500 text-slate-950 px-2.5 py-1 rounded-lg text-[10px] font-black uppercase flex items-center gap-1 shadow-md shadow-cyan-500/20"
                              >
                                <Plus className="w-3 h-3 stroke-[3]" /> Add
                              </button>
                            </div>
                          </div>

                          {/* Client Card List */}
                          <div className="space-y-2.5">
                            {clients.map(c => {
                              const bmiInfo = calculateBMI(c.currentWeight, c.heightCm);
                              return (
                                <div
                                  key={c.id}
                                  onClick={() => setSelectedClientId(c.id)}
                                  className={`p-3 rounded-2xl border transition cursor-pointer ${
                                    selectedClientId === c.id 
                                      ? 'bg-slate-900 border-cyan-500/50 shadow-md shadow-cyan-500/10' 
                                      : 'bg-slate-900/50 border-slate-800 hover:border-slate-700'
                                  }`}
                                >
                                  <div className="flex justify-between items-start">
                                    <div>
                                      <h5 className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                                        {c.name}
                                        {c.atGym && (
                                          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" title="At Gym" />
                                        )}
                                        <span className="text-[8px] bg-slate-800 text-indigo-300 px-1.5 py-0.2 rounded font-mono">
                                          {c.challengeLevel || 'Intermediate'}
                                        </span>
                                      </h5>
                                      <p className="text-[10px] text-slate-400">{c.email} • {c.gender || 'Male'} • {c.age || 25}y</p>
                                    </div>
                                    <div className="flex flex-col items-end gap-1">
                                      <div className="flex items-center gap-1">
                                        {c.atGym && (
                                          <span className="px-1.5 py-0.2 rounded text-[8px] font-extrabold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 flex items-center gap-0.5">
                                            <MapPin className="w-2.5 h-2.5" /> AT GYM
                                          </span>
                                        )}
                                        <span className={`px-2 py-0.5 rounded-full text-[9px] font-extrabold uppercase ${
                                          c.goalPhase === 'Cutting' ? 'bg-rose-500/20 text-rose-400 border border-rose-500/30' :
                                          c.goalPhase === 'Lean Bulking' ? 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/30' :
                                          c.goalPhase === 'Bulking' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' :
                                          'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                                        }`}>
                                          {c.goalPhase}
                                        </span>
                                      </div>
                                      <span className={`text-[8px] px-1.5 py-0.2 rounded-md font-bold border ${bmiInfo.bg} ${bmiInfo.color}`}>
                                        BMI {bmiInfo.bmi} ({bmiInfo.category})
                                      </span>
                                    </div>
                                  </div>

                                  {/* Approval Status Banner if pending */}
                                  {!c.approved && (
                                    <div className="mt-2.5 p-2 bg-amber-500/10 border border-amber-500/30 rounded-xl flex items-center justify-between text-[10px]">
                                      <span className="text-amber-400 font-bold">Pending Membership Approval</span>
                                      <div className="flex gap-1">
                                        <button onClick={(e) => { e.stopPropagation(); setClientApproval(c.id, true); }} className="px-2 py-0.5 bg-emerald-500 text-slate-950 font-bold rounded">Approve</button>
                                        <button onClick={(e) => { e.stopPropagation(); setClientApproval(c.id, false); }} className="px-2 py-0.5 bg-rose-500 text-slate-100 font-bold rounded">Deny</button>
                                      </div>
                                    </div>
                                  )}

                                  <div className="grid grid-cols-4 gap-1 mt-3 pt-2 border-t border-slate-800/60 text-center text-[10px]">
                                    <div>
                                      <span className="text-slate-500 block text-[8px] uppercase">Weight</span>
                                      <span className="font-bold text-slate-200">{c.currentWeight}kg</span>
                                    </div>
                                    <div>
                                      <span className="text-slate-500 block text-[8px] uppercase">Target</span>
                                      <span className="font-bold text-cyan-400">{c.targetWeight}kg</span>
                                    </div>
                                    <div>
                                      <span className="text-slate-500 block text-[8px] uppercase">Calories</span>
                                      <span className="font-bold text-amber-400">{c.dailyCalories}k</span>
                                    </div>
                                    <div>
                                      <span className="text-slate-500 block text-[8px] uppercase">Billing</span>
                                      <span className={`font-bold ${c.subscriptionDaysLeft <= 5 ? 'text-rose-400 animate-pulse' : 'text-emerald-400'}`}>
                                        {c.subscriptionDaysLeft}d left
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              );
                            })}
                          </div>
                        </div>
                      )}

                      {/* COACH: PROFILE & PROTOCOL EDITOR TAB */}
                      {activeTab === 'diet' && (
                        <div className="space-y-4">
                          <div className="bg-slate-900/90 border border-slate-800 p-3.5 rounded-2xl space-y-3">
                            <div className="flex justify-between items-center">
                              <div>
                                <p className="text-[9px] text-slate-400 font-bold uppercase">Editing Client Profile:</p>
                                <h4 className="text-xs font-black text-cyan-400">{activeClient.name}</h4>
                              </div>
                              <span className="text-[9px] font-mono text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
                                Sub: {activeClient.subscriptionDaysLeft}d remaining
                              </span>
                            </div>

                            {/* Editable Parameters */}
                            <div className="grid grid-cols-2 gap-2 text-[10px]">
                              <div>
                                <label className="text-[8px] font-bold text-slate-400 uppercase">Weight (kg)</label>
                                <input
                                  type="number"
                                  value={activeClient.currentWeight}
                                  onChange={(e) => saveClientData({ ...activeClient, currentWeight: parseFloat(e.target.value) || 0 })}
                                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-1.5 text-slate-200 font-bold"
                                />
                              </div>
                              <div>
                                <label className="text-[8px] font-bold text-slate-400 uppercase">Height (cm)</label>
                                <input
                                  type="number"
                                  value={activeClient.heightCm}
                                  onChange={(e) => saveClientData({ ...activeClient, heightCm: parseFloat(e.target.value) || 0 })}
                                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-1.5 text-slate-200 font-bold"
                                />
                              </div>
                              <div>
                                <label className="text-[8px] font-bold text-slate-400 uppercase">Gender</label>
                                <select
                                  value={activeClient.gender || 'Male'}
                                  onChange={(e) => saveClientData({ ...activeClient, gender: e.target.value })}
                                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-1.5 text-slate-200 font-bold"
                                >
                                  <option value="Male">Male</option>
                                  <option value="Female">Female</option>
                                  <option value="Other">Other</option>
                                </select>
                              </div>
                              <div>
                                <label className="text-[8px] font-bold text-slate-400 uppercase">Age</label>
                                <input
                                  type="number"
                                  value={activeClient.age || 25}
                                  onChange={(e) => saveClientData({ ...activeClient, age: parseInt(e.target.value) || 25 })}
                                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-1.5 text-slate-200 font-bold"
                                />
                              </div>
                            </div>

                            {/* Phase Switcher */}
                            <div>
                              <p className="text-[9px] font-bold text-slate-400 uppercase mb-1">Goal Phase Strategy</p>
                              <div className="grid grid-cols-4 gap-1">
                                {['Cutting', 'Maintaining', 'Lean Bulking', 'Bulking'].map(p => (
                                  <button
                                    key={p}
                                    onClick={() => handleUpdatePhase(p)}
                                    className={`py-1 text-[8px] font-black rounded-lg transition ${
                                      activeClient.goalPhase === p
                                        ? 'bg-cyan-500 text-slate-950'
                                        : 'bg-slate-950 text-slate-400 border border-slate-800'
                                    }`}
                                  >
                                    {p}
                                  </button>
                                ))}
                              </div>
                            </div>

                            {/* Challenge Level Switcher */}
                            <div>
                              <p className="text-[9px] font-bold text-slate-400 uppercase mb-1">Intensity Level</p>
                              <div className="grid grid-cols-4 gap-1">
                                {['Beginner', 'Intermediate', 'Advanced', 'Elite'].map(lvl => (
                                  <button
                                    key={lvl}
                                    onClick={() => handleUpdateChallengeLevel(lvl)}
                                    className={`py-1 text-[8px] font-bold rounded-lg transition ${
                                      (activeClient.challengeLevel || 'Intermediate') === lvl
                                        ? 'bg-indigo-500 text-slate-100 border border-indigo-400'
                                        : 'bg-slate-950 text-slate-400 border border-slate-800'
                                    }`}
                                  >
                                    {lvl}
                                  </button>
                                ))}
                              </div>
                            </div>
                          </div>

                          {/* Nutrition Customizer */}
                          <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl space-y-3">
                            <h5 className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                              <Utensils className="w-3.5 h-3.5 text-emerald-400" /> Meal Plan & Macros
                            </h5>

                            <div className="grid grid-cols-3 gap-2 text-center text-[10px]">
                              <div className="bg-slate-950 p-2 rounded-xl border border-slate-800">
                                <span className="text-slate-500 block text-[8px] uppercase">Prot (g)</span>
                                <input
                                  type="number"
                                  value={activeClient.proteinG}
                                  onChange={(e) => saveClientData({ ...activeClient, proteinG: parseInt(e.target.value) || 0 })}
                                  className="w-full text-center bg-transparent text-rose-400 font-extrabold focus:outline-none"
                                />
                              </div>
                              <div className="bg-slate-950 p-2 rounded-xl border border-slate-800">
                                <span className="text-slate-500 block text-[8px] uppercase">Carb (g)</span>
                                <input
                                  type="number"
                                  value={activeClient.carbsG}
                                  onChange={(e) => saveClientData({ ...activeClient, carbsG: parseInt(e.target.value) || 0 })}
                                  className="w-full text-center bg-transparent text-emerald-400 font-extrabold focus:outline-none"
                                />
                              </div>
                              <div className="bg-slate-950 p-2 rounded-xl border border-slate-800">
                                <span className="text-slate-500 block text-[8px] uppercase">Fat (g)</span>
                                <input
                                  type="number"
                                  value={activeClient.fatG}
                                  onChange={(e) => saveClientData({ ...activeClient, fatG: parseInt(e.target.value) || 0 })}
                                  className="w-full text-center bg-transparent text-cyan-400 font-extrabold focus:outline-none"
                                />
                              </div>
                            </div>

                            <div className="space-y-1.5">
                              {activeClient.dietPlan?.map(m => (
                                <div key={m.id} className="bg-slate-950 p-2 rounded-xl border border-slate-800 flex justify-between items-center text-[10px]">
                                  <div>
                                    <p className="font-bold text-slate-200">{m.name}</p>
                                    <p className="text-slate-500 text-[9px]">{m.desc}</p>
                                  </div>
                                  <button onClick={() => saveClientData({ ...activeClient, dietPlan: activeClient.dietPlan.filter(x => x.id !== m.id) })} className="text-rose-400 p-1">
                                    <Trash2 className="w-3 h-3" />
                                  </button>
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      )}

                      {/* COACH: WORKOUT ROUTINE TAB */}
                      {activeTab === 'workout' && (
                        <div className="space-y-4">
                          <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl space-y-3">
                            <h5 className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                              <Dumbbell className="w-3.5 h-3.5 text-indigo-400" /> Workout Routine ({activeClient.name})
                            </h5>

                            <div className="space-y-2">
                              {activeClient.workoutPlan?.map(ex => (
                                <div key={ex.id} className="bg-slate-950 p-2.5 rounded-xl border border-slate-800 flex justify-between items-center text-[10px]">
                                  <div>
                                    <p className="font-bold text-slate-200">{ex.exercise}</p>
                                    <p className="text-slate-500 font-mono text-[9px]">{ex.sets} sets × {ex.reps} reps ({ex.weight})</p>
                                  </div>
                                  <button onClick={() => saveClientData({ ...activeClient, workoutPlan: activeClient.workoutPlan.filter(w => w.id !== ex.id) })} className="text-rose-400 p-1">
                                    <Trash2 className="w-3 h-3" />
                                  </button>
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      )}

                      {/* COACH: AUDIT TRAIL LOG TAB */}
                      {activeTab === 'audit' && (
                        <div className="space-y-3">
                          <h4 className="text-xs font-extrabold text-slate-100 flex items-center gap-1.5">
                            <FileText className="w-4 h-4 text-cyan-400" /> Coach Audit Trail Log
                          </h4>
                          <div className="space-y-2">
                            {auditLogs.map(log => (
                              <div key={log.id} className="bg-slate-900/80 border border-slate-800 p-2.5 rounded-xl text-[10px] flex items-start gap-2">
                                <span className="font-mono text-cyan-400 shrink-0">{log.time}</span>
                                <p className="text-slate-300 leading-tight">{log.text}</p>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* COACH: BILLING TAB */}
                      {activeTab === 'billing' && (
                        <div className="space-y-3">
                          <h4 className="text-xs font-extrabold text-slate-100 flex items-center gap-1.5">
                            <Scale className="w-4 h-4 text-emerald-400" /> 30-Day Subscription Billing
                          </h4>
                          <div className="space-y-2">
                            {clients.map(c => (
                              <div key={c.id} className="bg-slate-900/90 border border-slate-800 p-3 rounded-xl flex justify-between items-center text-[10px]">
                                <div>
                                  <p className="font-bold text-slate-100">{c.name}</p>
                                  <p className={`font-mono text-[9px] ${c.subscriptionDaysLeft <= 5 ? 'text-rose-400 font-bold' : 'text-slate-400'}`}>
                                    {c.subscriptionDaysLeft} days remaining
                                  </p>
                                </div>
                                <button
                                  onClick={() => renewSubscription(c.id)}
                                  className="bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 px-3 py-1 rounded-lg font-bold"
                                >
                                  Renew 30 Days ($49)
                                </button>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </>
                  )}

                  {/* ========================================== */}
                  {/* 3. CLIENT ATHLETE PORTAL VIEWS             */}
                  {/* ========================================== */}
                  {userRole === 'client' && (
                    <>
                      {/* CLIENT HOME DASHBOARD */}
                      {activeTab === 'home' && (() => {
                        const clientBmi = calculateBMI(activeClient.currentWeight, activeClient.heightCm);
                        return (
                          <div className="space-y-4">
                            {/* Gym Check-in Interactive Toggle */}
                            <div className={`p-4 rounded-2xl border transition flex items-center justify-between ${
                              activeClient.atGym ? 'bg-emerald-500/10 border-emerald-500/40 shadow-lg shadow-emerald-500/10' : 'bg-slate-900 border-slate-800'
                            }`}>
                              <div>
                                <h5 className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                                  <MapPin className={`w-3.5 h-3.5 ${activeClient.atGym ? 'text-emerald-400' : 'text-slate-500'}`} />
                                  Gym Presence Status
                                </h5>
                                <p className="text-[10px] text-slate-400 mt-0.5">
                                  {activeClient.atGym ? 'Coach notified: You are currently at the gym!' : 'Toggle when you arrive at the facility'}
                                </p>
                              </div>
                              <button
                                onClick={toggleGymCheckIn}
                                className={`px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-wider transition ${
                                  activeClient.atGym 
                                    ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' 
                                    : 'bg-slate-800 text-slate-300 border border-slate-700'
                                }`}
                              >
                                {activeClient.atGym ? 'Checked In ✓' : 'I am at Gym'}
                              </button>
                            </div>

                            {/* Goal Phase & Challenge Level Banner */}
                            <div className="bg-gradient-to-tr from-slate-900 to-slate-850 p-4 rounded-2xl border border-slate-800 space-y-3">
                              <div className="flex justify-between items-center">
                                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Active Phase</span>
                                <div className="flex gap-1.5">
                                  <span className="px-2 py-0.5 rounded-full text-[9px] font-extrabold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 flex items-center gap-1">
                                    <Award className="w-2.5 h-2.5" /> {activeClient.challengeLevel || 'Intermediate'}
                                  </span>
                                  <span className={`px-2 py-0.5 rounded-full text-[9px] font-black uppercase ${
                                    activeClient.goalPhase === 'Cutting' ? 'bg-rose-500/20 text-rose-400 border border-rose-500/30' :
                                    activeClient.goalPhase === 'Lean Bulking' ? 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/30' :
                                    activeClient.goalPhase === 'Bulking' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' :
                                    'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                                  }`}>
                                    {activeClient.goalPhase}
                                  </span>
                                </div>
                              </div>

                              <div className="flex justify-between items-end">
                                <div>
                                  <p className="text-xl font-black text-slate-100">{activeClient.currentWeight} <span className="text-xs font-normal text-slate-400">kg</span></p>
                                  <p className="text-[9px] text-slate-400">Target: {activeClient.targetWeight} kg</p>
                                </div>
                                <div className="text-right font-mono">
                                  <p className="text-sm font-extrabold text-amber-400">{activeClient.dailyCalories} kcal</p>
                                  <p className="text-[9px] text-slate-500">Daily Energy Limit</p>
                                </div>
                              </div>
                            </div>

                            {/* Dynamic BMI Health Card */}
                            <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl flex items-center justify-between">
                              <div className="space-y-0.5">
                                <h5 className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                                  <Activity className="w-3.5 h-3.5 text-cyan-400" /> Body Mass Index (BMI)
                                </h5>
                                <p className="text-[9px] text-slate-400">Height: {activeClient.heightCm} cm | Weight: {activeClient.currentWeight} kg</p>
                              </div>
                              <div className="text-right">
                                <span className="text-lg font-black text-slate-100 font-mono">{clientBmi.bmi}</span>
                                <div className={`mt-0.5 px-2 py-0.5 rounded-md text-[8px] font-extrabold uppercase border ${clientBmi.bg} ${clientBmi.color}`}>
                                  {clientBmi.category}
                                </div>
                              </div>
                            </div>

                            {/* Quick Weight Logger */}
                            <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl space-y-2">
                              <h5 className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                                <Scale className="w-3.5 h-3.5 text-cyan-400" /> Log Daily Weight
                              </h5>
                              <form onSubmit={handleAddWeightLog} className="flex gap-2">
                                <input
                                  type="number"
                                  step="0.1"
                                  placeholder="Enter weight in kg"
                                  value={newWeightInput}
                                  onChange={(e) => setNewWeightInput(e.target.value)}
                                  className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-1.5 text-xs text-slate-200 focus:outline-none focus:border-cyan-500"
                                />
                                <button type="submit" className="bg-cyan-500 text-slate-950 font-bold px-3 py-1.5 rounded-xl text-xs">
                                  Log
                                </button>
                              </form>
                            </div>
                          </div>
                        );
                      })()}

                      {/* CLIENT NUTRITION TAB */}
                      {activeTab === 'diet' && (
                        <div className="space-y-3">
                          <h4 className="text-xs font-extrabold text-slate-100 flex items-center gap-1.5">
                            <Utensils className="w-4 h-4 text-emerald-400" /> Daily Meal Checklist
                          </h4>
                          <div className="space-y-2">
                            {activeClient.dietPlan?.map(m => (
                              <div
                                key={m.id}
                                onClick={() => toggleMeal(m.id)}
                                className={`p-3 rounded-xl border transition cursor-pointer flex justify-between items-start ${
                                  m.completed ? 'bg-emerald-500/10 border-emerald-500/30' : 'bg-slate-900/80 border-slate-800'
                                }`}
                              >
                                <div className="flex gap-2.5 items-start">
                                  <div className={`w-4 h-4 rounded border mt-0.5 flex items-center justify-center ${m.completed ? 'bg-emerald-500 border-emerald-400 text-slate-950' : 'border-slate-700 bg-slate-950'}`}>
                                    {m.completed && <Check className="w-3 h-3 stroke-[3]" />}
                                  </div>
                                  <div>
                                    <p className={`text-xs font-bold ${m.completed ? 'line-through text-slate-400' : 'text-slate-100'}`}>{m.name}</p>
                                    <p className="text-[10px] text-slate-400 mt-0.5">{m.desc}</p>
                                  </div>
                                </div>
                                <span className="text-[9px] font-mono text-cyan-400 bg-cyan-500/10 px-1.5 py-0.5 rounded">{m.time}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* CLIENT WORKOUT TAB */}
                      {activeTab === 'workout' && (
                        <div className="space-y-3">
                          <h4 className="text-xs font-extrabold text-slate-100 flex items-center gap-1.5">
                            <Dumbbell className="w-4 h-4 text-indigo-400" /> Daily Workout Protocol
                          </h4>
                          <div className="space-y-2">
                            {activeClient.workoutPlan?.map(w => (
                              <div
                                key={w.id}
                                onClick={() => toggleWorkout(w.id)}
                                className={`p-3 rounded-xl border transition cursor-pointer flex justify-between items-center ${
                                  w.completed ? 'bg-indigo-500/10 border-indigo-500/30' : 'bg-slate-900/80 border-slate-800'
                                }`}
                              >
                                <div className="flex gap-2.5 items-center">
                                  <div className={`w-4 h-4 rounded border flex items-center justify-center ${w.completed ? 'bg-indigo-500 border-indigo-400 text-slate-950' : 'border-slate-700 bg-slate-950'}`}>
                                    {w.completed && <Check className="w-3 h-3 stroke-[3]" />}
                                  </div>
                                  <div>
                                    <p className={`text-xs font-bold ${w.completed ? 'line-through text-slate-400' : 'text-slate-100'}`}>{w.exercise}</p>
                                    <p className="text-[10px] text-slate-400 font-mono">{w.sets} sets × {w.reps} reps</p>
                                  </div>
                                </div>
                                <span className="text-[10px] font-bold text-cyan-400 font-mono">{w.weight}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* CLIENT ANALYTICS TAB (30-day Bar & Pie charts) */}
                      {activeTab === 'analytics' && (
                        <div className="space-y-4">
                          <div className="flex justify-between items-center">
                            <h4 className="text-xs font-extrabold text-slate-100 flex items-center gap-1.5">
                              <BarChart2 className="w-4 h-4 text-cyan-400" /> 30-Day Progress Analytics
                            </h4>
                            <div className="flex bg-slate-900 rounded-lg p-0.5 border border-slate-800 text-[9px]">
                              <button
                                onClick={() => setAnalyticsView('bar')}
                                className={`px-2 py-1 rounded font-bold ${analyticsView === 'bar' ? 'bg-cyan-500 text-slate-950' : 'text-slate-400'}`}
                              >
                                Bar Chart
                              </button>
                              <button
                                onClick={() => setAnalyticsView('pie')}
                                className={`px-2 py-1 rounded font-bold ${analyticsView === 'pie' ? 'bg-cyan-500 text-slate-950' : 'text-slate-400'}`}
                              >
                                Pie Chart
                              </button>
                            </div>
                          </div>

                          {analyticsView === 'bar' ? (
                            <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl space-y-3">
                              <p className="text-[10px] text-slate-400 font-bold uppercase">Weight Trend Over Time (kg)</p>
                              <div className="h-36 flex items-end justify-between gap-2 pt-4 px-2 border-b border-slate-800">
                                {activeClient.weightHistory?.map((wh, idx) => {
                                  const heightPx = Math.min(100, Math.max(30, (wh.weight / 120) * 120));
                                  return (
                                    <div key={idx} className="flex-1 flex flex-col items-center gap-1">
                                      <span className="text-[8px] font-mono text-cyan-400">{wh.weight}</span>
                                      <div style={{ height: `${heightPx}px` }} className="w-full bg-gradient-to-t from-cyan-600 to-emerald-400 rounded-t-md" />
                                      <span className="text-[8px] text-slate-500">{wh.date}</span>
                                    </div>
                                  );
                                })}
                              </div>
                            </div>
                          ) : (
                            <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-2xl space-y-3 text-center">
                              <p className="text-[10px] text-slate-400 font-bold uppercase">Macro Nutrient Distribution</p>
                              <div className="w-28 h-28 rounded-full border-8 border-cyan-500 border-t-emerald-400 border-r-indigo-500 mx-auto flex items-center justify-center font-black text-xs text-slate-100">
                                {activeClient.dailyCalories} kcal
                              </div>
                              <div className="grid grid-cols-3 gap-2 text-[10px] pt-2">
                                <div className="bg-slate-950 p-1.5 rounded-lg border border-slate-800">
                                  <span className="text-rose-400 font-bold">Protein</span>
                                  <p className="font-ext5erbold">{activeClient.proteinG}g</p>
                                </div>
                                <div className="bg-slate-950 p-1.5 rounded-lg border border-slate-800">
                                  <span className="text-emerald-400 font-bold">Carbs</span>
                                  <p className="font-extrabold">{activeClient.carbsG}g</p>
                                </div>
                                <div className="bg-slate-950 p-1.5 rounded-lg border border-slate-800">
                                  <span className="text-cyan-400 font-bold">Fats</span>
                                  <p className="font-extrabold">{activeClient.fatG}g</p>
                                </div>
                              </div>
                            </div>
                          )}
                        </div>
                      )}
                    </>
                  )}

                </div>
              )}

              {/* ANDROID BOTTOM NAVIGATION BAR */}
              <div className="h-14 bg-slate-900 border-t border-slate-800/80 px-4 flex items-center justify-around absolute bottom-0 left-0 right-0 z-30">
                {userRole === 'admin' ? (
                  <>
                    <button
                      onClick={() => setActiveTab('roster')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'roster' ? 'text-cyan-400' : 'text-slate-500'}`}
                    >
                      <Users className="w-4 h-4" /> Roster
                    </button>
                    <button
                      onClick={() => setActiveTab('diet')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'diet' ? 'text-cyan-400' : 'text-slate-500'}`}
                    >
                      <Utensils className="w-4 h-4" /> Profile Edit
                    </button>
                    <button
                      onClick={() => setActiveTab('workout')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'workout' ? 'text-cyan-400' : 'text-slate-500'}`}
                    >
                      <Dumbbell className="w-4 h-4" /> Routine
                    </button>
                    <button
                      onClick={() => setActiveTab('audit')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'audit' ? 'text-cyan-400' : 'text-slate-500'}`}
                    >
                      <FileText className="w-4 h-4" /> Audit Log
                    </button>
                    <button
                      onClick={() => setActiveTab('billing')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'billing' ? 'text-cyan-400' : 'text-slate-500'}`}
                    >
                      <Scale className="w-4 h-4" /> Billing
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      onClick={() => setActiveTab('home')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'home' ? 'text-emerald-400' : 'text-slate-500'}`}
                    >
                      <Activity className="w-4 h-4" /> Overview
                    </button>
                    <button
                      onClick={() => setActiveTab('diet')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'diet' ? 'text-emerald-400' : 'text-slate-500'}`}
                    >
                      <Utensils className="w-4 h-4" /> Diet
                    </button>
                    <button
                      onClick={() => setActiveTab('workout')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'workout' ? 'text-emerald-400' : 'text-slate-500'}`}
                    >
                      <Dumbbell className="w-4 h-4" /> Routine
                    </button>
                    <button
                      onClick={() => setActiveTab('analytics')}
                      className={`flex flex-col items-center gap-0.5 text-[9px] font-bold ${activeTab === 'analytics' ? 'text-emerald-400' : 'text-slate-500'}`}
                    >
                      <BarChart2 className="w-4 h-4" /> Analytics
                    </button>
                  </>
                )}
              </div>
            </>
          )}

        </div>

        {/* ANDROID BOTTOM SYSTEM PILL NAVIGATION */}
        <div className="h-4 bg-slate-950 flex items-center justify-center shrink-0">
          <div className="w-32 h-1 bg-slate-700 rounded-full" />
        </div>
      </div>

      {/* AI GENERATOR MODAL (Coach Portal) */}
      {isAiModalOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-sm p-5 space-y-4 shadow-2xl">
            <div className="flex justify-between items-center">
              <h3 className="text-sm font-black text-slate-100 flex items-center gap-2">
                <Zap className="w-4 h-4 text-indigo-400" /> AI Plan Generator
              </h3>
              <button onClick={() => setIsAiModalOpen(false)} className="text-slate-400 hover:text-slate-100">✕</button>
            </div>

            <p className="text-[10px] text-slate-400">Generate customized diet and workout protocols for <strong className="text-cyan-400">{activeClient.name}</strong> based on biometrics & goals.</p>

            <div className="space-y-3 text-[10px]">
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-slate-400 block mb-1">Age</label>
                  <input
                    type="number"
                    value={aiForm.age}
                    onChange={(e) => setAiForm({ ...aiForm, age: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Gender</label>
                  <select
                    value={aiForm.gender}
                    onChange={(e) => setAiForm({ ...aiForm, gender: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  >
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-slate-400 block mb-1">Weight (kg)</label>
                  <input
                    type="number"
                    value={aiForm.weight}
                    onChange={(e) => setAiForm({ ...aiForm, weight: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Height (cm)</label>
                  <input
                    type="number"
                    value={aiForm.height}
                    onChange={(e) => setAiForm({ ...aiForm, height: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  />
                </div>
              </div>

              <div>
                <label className="text-slate-400 block mb-1">Goal Phase</label>
                <select
                  value={aiForm.goalPhase}
                  onChange={(e) => setAiForm({ ...aiForm, goalPhase: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                >
                  <option value="Cutting">Cutting (Fat Loss)</option>
                  <option value="Maintaining">Maintaining</option>
                  <option value="Lean Bulking">Lean Bulking (Clean Surplus)</option>
                  <option value="Bulking">Bulking (Mass Gain)</option>
                </select>
              </div>

              <div>
                <label className="text-slate-400 block mb-1">Challenge Level</label>
                <select
                  value={aiForm.challengeLevel}
                  onChange={(e) => setAiForm({ ...aiForm, challengeLevel: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                >
                  <option value="Beginner">Beginner</option>
                  <option value="Intermediate">Intermediate</option>
                  <option value="Advanced">Advanced</option>
                  <option value="Elite">Elite</option>
                </select>
              </div>

              <button
                onClick={runAiPlanGenerator}
                className="w-full bg-gradient-to-r from-indigo-500 to-cyan-500 text-slate-950 font-black py-2.5 rounded-xl text-xs uppercase tracking-wider shadow-lg mt-2"
              >
                Generate & Apply AI Plan
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ADD NEW CLIENT MODAL */}
      {isAddingClientModal && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-sm p-5 space-y-4 shadow-2xl">
            <div className="flex justify-between items-center">
              <h3 className="text-sm font-black text-slate-100 flex items-center gap-2">
                <Users className="w-4 h-4 text-cyan-400" /> Add New Client Profile
              </h3>
              <button onClick={() => setIsAddingClientModal(false)} className="text-slate-400 hover:text-slate-100">✕</button>
            </div>

            <form onSubmit={handleAddNewClientSubmit} className="space-y-3 text-[10px]">
              <div>
                <label className="text-slate-400 block mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={newClientForm.name}
                  onChange={(e) => setNewClientForm({ ...newClientForm, name: e.target.value })}
                  placeholder="John Doe"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                />
              </div>

              <div>
                <label className="text-slate-400 block mb-1">Email Address</label>
                <input
                  type="email"
                  required
                  value={newClientForm.email}
                  onChange={(e) => setNewClientForm({ ...newClientForm, email: e.target.value })}
                  placeholder="john@fitgym.com"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-slate-400 block mb-1">Gender</label>
                  <select
                    value={newClientForm.gender}
                    onChange={(e) => setNewClientForm({ ...newClientForm, gender: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  >
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Age</label>
                  <input
                    type="number"
                    value={newClientForm.age}
                    onChange={(e) => setNewClientForm({ ...newClientForm, age: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-slate-400 block mb-1">Weight (kg)</label>
                  <input
                    type="number"
                    value={newClientForm.currentWeight}
                    onChange={(e) => setNewClientForm({ ...newClientForm, currentWeight: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Height (cm)</label>
                  <input
                    type="number"
                    value={newClientForm.heightCm}
                    onChange={(e) => setNewClientForm({ ...newClientForm, heightCm: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2 text-slate-200"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full bg-cyan-500 text-slate-950 font-black py-2.5 rounded-xl text-xs uppercase tracking-wider shadow-lg mt-2"
              >
                Create Client Account
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}