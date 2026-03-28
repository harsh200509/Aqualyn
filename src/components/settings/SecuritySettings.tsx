import React, { useState } from 'react';
import { Shield, Smartphone, Download, Trash2, MonitorSmartphone, Lock, Key } from 'lucide-react';
import { useAppContext } from '../../context/AppContext';
import { motion, AnimatePresence } from 'motion/react';

export default function SecuritySettings() {
  const { appLockPin, setAppLockPin, archiveLockPin, setArchiveLockPin, addToast } = useAppContext();
  const [isPinModalOpen, setIsPinModalOpen] = useState(false);
  const [pinType, setPinType] = useState<'app' | 'archive'>('app');
  const [pinValue, setPinValue] = useState('');

  const handleSetPin = () => {
    if (pinValue.length < 4) {
      addToast('PIN must be at least 4 digits', 'error');
      return;
    }
    if (pinType === 'app') setAppLockPin(pinValue);
    else setArchiveLockPin(pinValue);
    
    addToast(`${pinType === 'app' ? 'App' : 'Archive'} PIN set successfully`, 'success');
    setIsPinModalOpen(false);
    setPinValue('');
  };

  return (
    <section className="space-y-4 pb-12">
      <h3 className="font-headline font-bold text-lg text-on-surface px-2 flex items-center gap-2">
        <Shield className="w-5 h-5 text-indigo-500" />
        Security & Data
      </h3>
      
      <div className="glass-card rounded-[2rem] overflow-hidden border border-white/40 shadow-sm">
        <div 
          onClick={() => { setPinType('app'); setIsPinModalOpen(true); }}
          className="p-5 border-b border-white/20 hover:bg-white/40 transition-colors cursor-pointer flex items-center justify-between group"
        >
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-xl bg-indigo-50 flex items-center justify-center text-indigo-500 group-hover:bg-indigo-100 transition-colors">
              <Lock className="w-5 h-5" />
            </div>
            <div>
              <h4 className="font-semibold text-on-surface">App Lock</h4>
              <p className="text-sm text-on-surface-variant">{appLockPin ? 'PIN is set' : 'Protect app with a PIN'}</p>
            </div>
          </div>
          {appLockPin && <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-md">Active</span>}
        </div>

        <div 
          onClick={() => { setPinType('archive'); setIsPinModalOpen(true); }}
          className="p-5 border-b border-white/20 hover:bg-white/40 transition-colors cursor-pointer flex items-center justify-between group"
        >
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-xl bg-amber-50 flex items-center justify-center text-amber-500 group-hover:bg-amber-100 transition-colors">
              <Key className="w-5 h-5" />
            </div>
            <div>
              <h4 className="font-semibold text-on-surface">Archive Lock</h4>
              <p className="text-sm text-on-surface-variant">{archiveLockPin ? 'PIN is set' : 'Lock archived chats with a PIN'}</p>
            </div>
          </div>
          {archiveLockPin && <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-md">Active</span>}
        </div>

        <div className="p-5 border-b border-white/20 hover:bg-white/40 transition-colors cursor-pointer flex items-center justify-between group">
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-xl bg-indigo-50 flex items-center justify-center text-indigo-500 group-hover:bg-indigo-100 transition-colors">
              <MonitorSmartphone className="w-5 h-5" />
            </div>
            <div>
              <h4 className="font-semibold text-on-surface">Linked Devices</h4>
              <p className="text-sm text-on-surface-variant">Use Aqualyn on desktop or web</p>
            </div>
          </div>
          <span className="text-xs font-bold bg-indigo-100 text-indigo-600 px-2 py-1 rounded-md">2 Devices</span>
        </div>
        
        <div className="p-5 border-b border-white/20 hover:bg-white/40 transition-colors cursor-pointer flex items-center justify-between group">
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center text-emerald-500 group-hover:bg-emerald-100 transition-colors">
              <Download className="w-5 h-5" />
            </div>
            <div>
              <h4 className="font-semibold text-on-surface">Export Data</h4>
              <p className="text-sm text-on-surface-variant">Download all your chats and media</p>
            </div>
          </div>
        </div>
        <div className="p-5 hover:bg-red-50/50 transition-colors cursor-pointer flex items-center justify-between group">
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-xl bg-red-50 flex items-center justify-center text-red-500 group-hover:bg-red-100 transition-colors">
              <Trash2 className="w-5 h-5" />
            </div>
            <div>
              <h4 className="font-semibold text-red-600">Deactivate Account</h4>
              <p className="text-sm text-red-500/80">Permanently delete your data</p>
            </div>
          </div>
        </div>
      </div>

      <AnimatePresence>
        {isPinModalOpen && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-6">
            <motion.div 
              initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
              className="absolute inset-0 bg-black/40 backdrop-blur-sm"
              onClick={() => setIsPinModalOpen(false)}
            />
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 20 }} 
              animate={{ opacity: 1, scale: 1, y: 0 }} 
              exit={{ opacity: 0, scale: 0.9, y: 20 }}
              className="relative w-full max-w-sm glass-card rounded-[2.5rem] p-8 shadow-2xl border border-white/40"
            >
              <h3 className="text-2xl font-black font-headline text-on-surface mb-2">Set {pinType === 'app' ? 'App' : 'Archive'} PIN</h3>
              <p className="text-on-surface-variant text-sm mb-6">Enter a 4-digit PIN to secure your {pinType === 'app' ? 'application' : 'archived chats'}.</p>
              
              <input 
                type="password" 
                maxLength={4}
                value={pinValue}
                onChange={(e) => setPinValue(e.target.value.replace(/\D/g, ''))}
                placeholder="••••"
                className="w-full h-16 text-center text-3xl tracking-[1rem] font-bold rounded-2xl bg-white/50 border border-white/40 focus:ring-2 focus:ring-primary/20 outline-none mb-6"
              />
              
              <div className="flex gap-3">
                <button 
                  onClick={() => setIsPinModalOpen(false)}
                  className="flex-1 py-4 rounded-2xl font-bold text-on-surface-variant hover:bg-white/40 transition-colors"
                >
                  Cancel
                </button>
                <button 
                  onClick={handleSetPin}
                  className="flex-1 py-4 rounded-2xl bg-primary text-white font-bold shadow-lg shadow-primary/20 hover:brightness-110 active:scale-95 transition-all"
                >
                  Set PIN
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </section>
  );
}
