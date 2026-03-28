import React, { useState } from 'react';
import { AnimatePresence } from 'motion/react';
import LoginScreen from './screens/LoginScreen';
import ChatListScreen from './screens/ChatListScreen';
import ChatDetailScreen from './screens/ChatDetailScreen';
import ProfileScreen from './screens/ProfileScreen';
import SettingsScreen from './screens/SettingsScreen';
import ContactsScreen from './screens/ContactsScreen';
import EditProfileScreen from './screens/EditProfileScreen';
import ContactProfileScreen from './screens/ContactProfileScreen';
import StoriesScreen from './screens/StoriesScreen';
import BottomNav from './components/BottomNav';
import ToastContainer from './components/ui/ToastContainer';
import AppLockScreen from './components/AppLockScreen';
import { useAppContext } from './context/AppContext';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState('login');
  const { isAppLocked, appLockPin } = useAppContext();

  return (
    <div className="min-h-screen bg-surface text-on-surface font-body selection:bg-secondary-container/30 overflow-x-hidden">
      <ToastContainer />
      
      {appLockPin && isAppLocked ? (
        <AppLockScreen />
      ) : (
        <>
          <AnimatePresence mode="wait">
            {currentScreen === 'login' && <LoginScreen key="login" onLogin={() => setCurrentScreen('chats')} />}
            {currentScreen === 'chats' && <ChatListScreen key="chats" onNavigate={setCurrentScreen} />}
            {currentScreen === 'chat-detail' && <ChatDetailScreen key="chat-detail" onBack={() => setCurrentScreen('chats')} onNavigate={setCurrentScreen} />}
            {currentScreen === 'profile' && <ProfileScreen key="profile" onNavigate={setCurrentScreen} />}
            {currentScreen === 'settings' && <SettingsScreen key="settings" onBack={() => setCurrentScreen('profile')} onNavigate={setCurrentScreen} />}
            {currentScreen === 'contacts' && <ContactsScreen key="contacts" onNavigate={setCurrentScreen} />}
            {currentScreen === 'edit-profile' && <EditProfileScreen key="edit-profile" onBack={() => setCurrentScreen('profile')} />}
            {currentScreen === 'contact-profile' && <ContactProfileScreen key="contact-profile" onBack={() => setCurrentScreen('contacts')} onNavigate={setCurrentScreen} />}
            {currentScreen === 'stories' && <StoriesScreen key="stories" onBack={() => setCurrentScreen('chats')} onNavigate={setCurrentScreen} />}
          </AnimatePresence>
          
          {currentScreen !== 'login' && currentScreen !== 'chat-detail' && currentScreen !== 'contact-profile' && currentScreen !== 'edit-profile' && (
            <BottomNav currentScreen={currentScreen} onNavigate={setCurrentScreen} />
          )}
        </>
      )}
    </div>
  );
}
