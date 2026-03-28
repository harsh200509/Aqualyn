import React, { useState, useRef } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, Save, Camera } from 'lucide-react';
import { useAppContext } from '../context/AppContext';

export default function EditProfileScreen({ onBack }: { onBack: () => void }) {
  const { currentUser, setCurrentUser, addToast } = useAppContext();
  
  const [name, setName] = useState(currentUser?.name || '');
  const [username, setUsername] = useState('@' + (currentUser?.name?.toLowerCase().replace(/\s+/g, '') || 'user'));
  const [role, setRole] = useState(currentUser?.role || '');
  const [bio, setBio] = useState(currentUser?.bio || '');
  const [avatar, setAvatar] = useState(currentUser?.largeAvatar || currentUser?.avatar || '');
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  if (!currentUser) return null;

  const handleSave = () => {
    setCurrentUser({
      ...currentUser,
      name,
      role,
      bio,
      avatar,
      largeAvatar: avatar
    });
    addToast('Profile updated successfully', 'success');
    onBack();
  };

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Create a local object URL for the selected image
      const imageUrl = URL.createObjectURL(file);
      setAvatar(imageUrl);
    }
  };

  return (
    <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 20 }} className="bg-surface min-h-screen pb-32">
      <header className="fixed top-0 w-full z-50 bg-slate-50/70 backdrop-blur-xl border-b border-white/15 h-16 flex items-center justify-between px-6">
        <div className="flex items-center gap-4">
          <button onClick={onBack} className="text-slate-500 hover:bg-white/20 p-2 rounded-full transition-colors active:scale-95 duration-200">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="font-headline tracking-tight font-bold text-lg text-on-surface">Edit Profile</h1>
        </div>
        <button onClick={handleSave} className="text-primary font-bold flex items-center gap-2 hover:opacity-80">
          <Save className="w-5 h-5" />
          Save
        </button>
      </header>

      <main className="pt-24 px-6 max-w-2xl mx-auto space-y-8">
        <div className="flex flex-col items-center gap-4">
          <div 
            className="w-32 h-32 rounded-full border-4 border-white shadow-lg overflow-hidden relative group cursor-pointer"
            onClick={() => fileInputRef.current?.click()}
          >
            <img src={avatar} alt="Profile" className="w-full h-full object-cover" />
            <div className="absolute inset-0 bg-black/40 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <Camera className="w-6 h-6 text-white mb-1" />
              <span className="text-white text-xs font-bold">Change</span>
            </div>
          </div>
          <input 
            type="file" 
            ref={fileInputRef} 
            onChange={handleAvatarChange} 
            accept="image/*" 
            className="hidden" 
          />
        </div>

        <div className="space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-bold text-on-surface-variant ml-1">Display Name</label>
            <input 
              type="text" 
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full h-14 bg-white/50 border border-white/40 rounded-2xl px-4 focus:ring-2 focus:ring-secondary/30 focus:border-secondary outline-none text-on-surface"
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-bold text-on-surface-variant ml-1">Username</label>
            <input 
              type="text" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full h-14 bg-white/50 border border-white/40 rounded-2xl px-4 focus:ring-2 focus:ring-secondary/30 focus:border-secondary outline-none text-on-surface"
            />
            <p className="text-xs text-on-surface-variant ml-1">You can choose a username on Aqualyn. If you do, people will be able to find you by this username and contact you without needing your phone number.</p>
          </div>
          <div className="space-y-2">
            <label className="text-sm font-bold text-on-surface-variant ml-1">Role / Title</label>
            <input 
              type="text" 
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className="w-full h-14 bg-white/50 border border-white/40 rounded-2xl px-4 focus:ring-2 focus:ring-secondary/30 focus:border-secondary outline-none text-on-surface"
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-bold text-on-surface-variant ml-1">About</label>
            <textarea 
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              rows={4}
              placeholder="Hey there! I am using Aqualyn."
              className="w-full bg-white/50 border border-white/40 rounded-2xl p-4 focus:ring-2 focus:ring-secondary/30 focus:border-secondary outline-none text-on-surface resize-none"
            />
          </div>
        </div>
      </main>
    </motion.div>
  );
}
