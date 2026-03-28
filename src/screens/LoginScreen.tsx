import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Droplet, AtSign, ArrowRight, Globe, Shield, Headset, Phone } from 'lucide-react';

export default function LoginScreen({ onLogin }: { onLogin: () => void }) {
  const [step, setStep] = useState<'intro' | 'input' | 'otp'>('intro');
  const [contactMethod, setContactMethod] = useState('');
  const [otp, setOtp] = useState(['', '', '', '', '', '']);

  const handleNext = () => {
    if (step === 'intro') setStep('input');
    else if (step === 'input' && contactMethod) setStep('otp');
    else if (step === 'otp') onLogin();
  };

  const handleOtpChange = (index: number, value: string) => {
    if (value.length > 1) return;
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);
    
    // Auto-focus next input
    if (value && index < 5) {
      const nextInput = document.getElementById(`otp-${index + 1}`);
      nextInput?.focus();
    }
  };

  return (
    <motion.div 
      initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
      className="min-h-screen flex items-center justify-center liquid-bg px-4 py-8 relative overflow-hidden"
    >
      <div className="absolute -top-[10%] -left-[10%] w-[50%] h-[50%] rounded-full bg-secondary-fixed/20 blur-[120px] pointer-events-none"></div>
      <div className="absolute top-[40%] -right-[5%] w-[40%] h-[40%] rounded-full bg-primary-container/20 blur-[100px] pointer-events-none"></div>

      <div className="relative z-10 w-full max-w-[420px] flex flex-col items-center">
        <AnimatePresence mode="wait">
          {step === 'intro' && (
            <motion.div 
              key="intro"
              initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }}
              className="flex flex-col items-center text-center w-full"
            >
              <div className="w-24 h-24 mb-8 relative group">
                <div className="absolute inset-0 bg-gradient-to-br from-secondary-fixed to-primary-container rounded-[2rem] rotate-12 opacity-30 group-hover:rotate-45 transition-transform duration-700"></div>
                <div className="relative w-full h-full bg-surface-container-lowest rounded-[2rem] flex items-center justify-center glass-card inner-glow aqua-glow shadow-2xl">
                  <Droplet className="text-secondary w-12 h-12 fill-secondary" />
                </div>
              </div>
              <h1 className="font-headline text-5xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-br from-primary to-secondary mb-4">Aqualyn</h1>
              <p className="font-body text-on-surface-variant text-lg font-medium tracking-wide mb-12 max-w-xs leading-relaxed">
                Crystal clear communication for modern professionals.
              </p>
              <button onClick={handleNext} className="w-full h-14 bg-gradient-to-br from-secondary to-primary-container text-white font-headline font-bold rounded-full shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2 text-lg">
                Get Started
                <ArrowRight className="w-5 h-5" />
              </button>
            </motion.div>
          )}

          {step === 'input' && (
            <motion.div 
              key="input"
              initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}
              className="w-full glass-card border border-white/30 rounded-[2.5rem] p-8 sm:p-10 inner-glow shadow-2xl"
            >
              <div className="mb-8">
                <h2 className="text-2xl font-bold font-headline text-on-surface mb-2">Welcome Back</h2>
                <p className="text-on-surface-variant text-sm">Enter your phone number or email to continue.</p>
              </div>

              <div className="space-y-6">
                <div className="space-y-2">
                  <label className="font-label text-sm font-semibold text-on-surface-variant ml-1">Email or Phone</label>
                  <div className="relative group">
                    <input 
                      type="text" 
                      value={contactMethod}
                      onChange={(e) => setContactMethod(e.target.value)}
                      placeholder="name@company.com or +1 234..." 
                      className="w-full h-14 bg-white/40 border-outline-variant/20 border rounded-2xl pl-12 pr-4 focus:ring-2 focus:ring-secondary/40 focus:border-secondary transition-all outline-none placeholder:text-on-surface-variant/50 font-body text-on-surface shadow-inner"
                      autoFocus
                    />
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-secondary transition-colors">
                      {contactMethod.includes('@') ? <AtSign className="w-5 h-5" /> : <Phone className="w-5 h-5" />}
                    </div>
                  </div>
                </div>

                <button 
                  onClick={handleNext} 
                  disabled={!contactMethod}
                  className="w-full h-14 bg-gradient-to-br from-secondary to-primary-container text-white font-headline font-bold rounded-2xl shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:pointer-events-none"
                >
                  Continue
                  <ArrowRight className="w-5 h-5" />
                </button>
              </div>
            </motion.div>
          )}

          {step === 'otp' && (
            <motion.div 
              key="otp"
              initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}
              className="w-full glass-card border border-white/30 rounded-[2.5rem] p-8 sm:p-10 inner-glow shadow-2xl"
            >
              <div className="mb-8">
                <h2 className="text-2xl font-bold font-headline text-on-surface mb-2">Verify it's you</h2>
                <p className="text-on-surface-variant text-sm">We sent a 6-digit code to <span className="font-semibold text-on-surface">{contactMethod}</span></p>
              </div>

              <div className="space-y-8">
                <div className="flex justify-between gap-2 sm:gap-3">
                  {otp.map((digit, i) => (
                    <input 
                      key={i} 
                      id={`otp-${i}`}
                      type="text" 
                      maxLength={1} 
                      value={digit}
                      onChange={(e) => handleOtpChange(i, e.target.value)}
                      className="w-10 h-12 sm:w-12 sm:h-14 glass-card bg-white/50 border border-white/40 rounded-xl sm:rounded-2xl text-center text-xl sm:text-2xl font-headline font-bold text-primary focus:ring-2 focus:ring-secondary/50 focus:border-secondary transition-all outline-none shadow-inner" 
                      autoFocus={i === 0}
                    />
                  ))}
                </div>

                <div className="flex flex-col gap-4">
                  <button 
                    onClick={handleNext} 
                    disabled={otp.some(d => !d)}
                    className="w-full h-14 bg-gradient-to-br from-secondary to-primary-container text-white font-headline font-bold rounded-2xl shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:pointer-events-none"
                  >
                    Verify & Enter
                    <ArrowRight className="w-5 h-5" />
                  </button>
                  <button className="text-sm font-bold text-secondary hover:text-primary transition-colors text-center">
                    Resend Code
                  </button>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {step !== 'intro' && (
          <footer className="mt-12 text-center w-full">
            <div className="flex items-center justify-center gap-6">
              {[Globe, Shield, Headset].map((Icon, i) => (
                <div key={i} className="w-10 h-10 glass-card rounded-xl flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors cursor-pointer border border-white/20">
                  <Icon className="w-5 h-5" />
                </div>
              ))}
            </div>
          </footer>
        )}
      </div>
      
      <div className="fixed bottom-0 left-0 p-6 sm:p-8 flex items-center gap-3">
        <div className="w-3 h-3 rounded-full bg-secondary-fixed shadow-[0_0_10px_#0bfbff] animate-pulse"></div>
        <span className="text-[10px] font-bold tracking-[0.2em] text-on-surface-variant uppercase">Network Secure</span>
      </div>
    </motion.div>
  );
}
