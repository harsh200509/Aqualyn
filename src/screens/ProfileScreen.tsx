import React from 'react';
import { motion } from 'motion/react';
import { Pen, Plus } from 'lucide-react';
import { useAppContext } from '../context/AppContext';

export default function ProfileScreen({ onNavigate }: { onNavigate: (s: string) => void }) {
  const { currentUser } = useAppContext();

  if (!currentUser) return null;

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="bg-surface min-h-screen pb-32">
      <header className="fixed top-0 w-full z-50 bg-slate-50/70 backdrop-blur-xl border-b border-white/15 shadow-[0_8px_32px_0_rgba(0,87,189,0.06)] h-16 flex items-center justify-between px-6">
        <div className="flex items-center gap-3">
          <span className="text-2xl font-black bg-gradient-to-br from-cyan-600 to-blue-500 bg-clip-text text-transparent font-headline tracking-tight">Aqualyn</span>
        </div>
        <div className="flex items-center gap-4">
          <div className="w-10 h-10 rounded-full border-2 border-secondary-fixed aqua-glow overflow-hidden">
            <img src={currentUser.avatar} alt="Profile" className="w-full h-full object-cover" />
          </div>
        </div>
      </header>

      <main className="pt-24 px-6 max-w-4xl mx-auto space-y-10">
        <section className="relative flex flex-col md:flex-row items-center md:items-end gap-8">
          <div className="relative group">
            <div className="w-40 h-40 md:w-48 md:h-48 rounded-full border-[6px] border-white aqua-glow overflow-hidden bg-surface-container shadow-xl">
              <img src={currentUser.largeAvatar} alt="Large Profile" className="w-full h-full object-cover" />
            </div>
            <button onClick={() => onNavigate('edit-profile')} className="absolute bottom-2 right-2 bg-secondary text-on-secondary w-10 h-10 rounded-full flex items-center justify-center shadow-lg hover:scale-110 transition-transform active:scale-90">
              <Pen className="w-5 h-5 fill-on-secondary" />
            </button>
          </div>
          <div className="flex-1 text-center md:text-left space-y-4">
            <div className="space-y-1">
              <h1 className="text-4xl font-extrabold font-headline tracking-tight text-on-surface">{currentUser.name}</h1>
            </div>
            <p className="text-on-surface/80 max-w-md leading-relaxed">{currentUser.bio}</p>
            <div className="flex flex-wrap justify-center md:justify-start gap-3 pt-2">
              <button onClick={() => onNavigate('edit-profile')} className="px-8 py-3 rounded-full bg-gradient-to-br from-secondary to-primary-container text-on-primary-container font-bold text-sm shadow-lg hover:brightness-110 active:scale-95 transition-all border-t border-white/20">
                Edit Profile
              </button>
              <button className="px-6 py-3 rounded-full glass-panel border border-outline-variant/15 text-on-surface font-semibold text-sm hover:bg-white/40 transition-colors active:scale-95">
                Share Profile
              </button>
            </div>
          </div>
        </section>

        <section className="space-y-4">
          <div className="flex items-center justify-between px-2">
            <h2 className="font-headline font-bold text-xl text-on-surface">Recent Stories</h2>
            <button onClick={() => onNavigate('stories')} className="text-primary font-bold text-sm hover:text-primary-dim transition-colors">View All</button>
          </div>
          <div className="flex gap-4 overflow-x-auto pb-4 scrollbar-hide snap-x">
            <div className="min-w-[120px] h-[180px] rounded-3xl border-2 border-dashed border-outline-variant/30 flex flex-col items-center justify-center gap-2 text-on-surface-variant hover:bg-white/40 transition-colors cursor-pointer snap-start">
              <div className="w-12 h-12 rounded-full bg-primary-container/30 flex items-center justify-center">
                <Plus className="w-6 h-6 text-primary" />
              </div>
              <span className="font-semibold text-sm">Add Story</span>
            </div>
            {[1, 2, 3].map((i) => (
              <div key={i} className="min-w-[120px] h-[180px] rounded-3xl overflow-hidden relative group cursor-pointer snap-start shadow-sm">
                <img src={`https://images.unsplash.com/photo-1558655146-d09347e92766?auto=format&fit=crop&q=80&w=300&h=450&sig=${i}`} alt="Story" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                <span className="absolute bottom-3 left-3 text-white font-medium text-xs">2h ago</span>
              </div>
            ))}
          </div>
        </section>
      </main>
    </motion.div>
  );
}
