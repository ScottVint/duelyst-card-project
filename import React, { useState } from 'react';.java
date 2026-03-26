import React, { useState } from 'react';
import { 
  Settings, 
  PlayCircle, 
  User, 
  Layers, 
  Zap, 
  Move, 
  CheckSquare, 
  ChevronDown,
  Cpu
} from 'lucide-react';

const FlowStep = ({ icon: Icon, title, details, subtasks, isActive, isCompleted, onClick }) => {
  return (
    <div 
      onClick={onClick}
      className={`group relative flex flex-col items-center transition-all duration-300 cursor-pointer ${isActive ? 'scale-105' : 'hover:scale-102'}`}
    >
      {/* Node Container */}
      <div className={`
        w-72 p-5 rounded-2xl border-2 shadow-sm transition-all
        ${isActive ? 'bg-blue-50 border-blue-500 shadow-blue-100 dark:bg-blue-900/20 dark:border-blue-400' : 
          isCompleted ? 'bg-emerald-50 border-emerald-300 dark:bg-emerald-900/10 dark:border-emerald-800' : 
          'bg-white border-gray-100 dark:bg-gray-900 dark:border-gray-800'}
      `}>
        <div className="flex items-center gap-3 mb-3">
          <div className={`
            p-2 rounded-lg transition-colors
            ${isActive ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-500 dark:bg-gray-800 dark:text-gray-400'}
          `}>
            <Icon size={20} />
          </div>
          <h3 className={`font-bold text-sm ${isActive ? 'text-blue-700 dark:text-blue-300' : 'text-gray-700 dark:text-gray-200'}`}>
            {title}
          </h3>
        </div>

        <p className="text-xs text-gray-500 dark:text-gray-400 leading-relaxed mb-3">
          {details}
        </p>

        {isActive && (
          <div className="space-y-1.5 border-t border-blue-100 dark:border-blue-800 pt-3 animate-in fade-in slide-in-from-top-1">
            {subtasks.map((task, i) => (
              <div key={i} className="flex items-start gap-2 text-[11px] text-blue-600 dark:text-blue-400">
                <CheckSquare size={12} className="mt-0.5 shrink-0" />
                <span>{task}</span>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Connection Arrow */}
      <div className="h-10 flex flex-col items-center justify-center text-gray-300 dark:text-gray-700">
        <div className="w-0.5 h-full bg-current"></div>
        <ChevronDown size={16} className="-mt-1" />
      </div>
    </div>
  );
};

const App = () => {
  const [currentStep, setCurrentStep] = useState(0);

  const steps = [
    {
      title: "1. Initialization",
      icon: Settings,
      details: "GameEngine configures the 9x5 board and places initial units.",
      subtasks: [
        "Set up 9x5 grid layout",
        "Place Player 1 Avatar at [2,3]",
        "Draw 3 cards from deck to hand"
      ]
    },
    {
      title: "2. Turn Start Phase",
      icon: PlayCircle,
      details: "System transfers control to the human player and refreshes resources.",
      subtasks: [
        "Set active status to Human",
        "Player draws +1 card",
        "Set Mana to 2 (Turn 1 + 1)"
      ]
    },
    {
      title: "3. Summoning Action",
      icon: Layers,
      details: "Player spends mana to place a creature unit on the board.",
      subtasks: [
        "CardActionHandler verifies mana cost",
        "MovementLogic highlights adjacent tiles",
        "Update board and deduct mana"
      ]
    },
    {
      title: "4. Movement Logic",
      icon: Move,
      details: "Player commands the Avatar or a unit to reposition.",
      subtasks: [
        "Highlight valid paths (2 cardinal / 1 diagonal)",
        "Update unit position on grid",
        "Set hasMoved = true for the unit"
      ]
    },
    {
      title: "5. Turn Transition",
      icon: User,
      details: "Clear turn-specific data and pass control to the AI.",
      subtasks: [
        "Player clicks 'End Turn'",
        "Clear any unspent mana pool",
        "Transfer control to AIPlayer"
      ]
    }
  ];

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 py-12 px-4 font-sans">
      <div className="max-w-2xl mx-auto">
        <header className="text-center mb-12">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 text-xs font-bold mb-4">
            <Zap size={14} />
            PROCESS FLOW VISUALIZATION
          </div>
          <h1 className="text-3xl font-black text-slate-900 dark:text-white mb-2">
            Human Player's First Valid Turn
          </h1>
          <p className="text-slate-500 dark:text-slate-400 text-sm">
            Click on each node to view detailed logic subtasks and triggers.
          </p>
        </header>

        <div className="flex flex-col items-center">
          {steps.map((step, index) => (
            <FlowStep 
              key={index}
              {...step}
              isActive={currentStep === index}
              isCompleted={currentStep > index}
              onClick={() => setCurrentStep(index)}
            />
          ))}
          
          {/* AI Entry Point */}
          <div className="w-72 p-5 rounded-2xl border-2 border-dashed border-slate-200 dark:border-slate-800 flex flex-col items-center justify-center text-center opacity-60">
            <Cpu size={24} className="text-slate-400 mb-2" />
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">AI Control Phase</h3>
            <p className="text-[10px] text-slate-400 mt-1 italic">Waiting for AI decision tree execution...</p>
          </div>
        </div>

        {/* Legend */}
        <div className="mt-12 flex items-center justify-center gap-6">
          <div className="flex items-center gap-2">
            <div className="w-2.5 h-2.5 rounded-full bg-blue-500"></div>
            <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-tighter">Current Focus</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-400"></div>
            <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-tighter">Executed Logic</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default App;