# Resume Builder - Professional Web App

A complete, modern web application for creating, editing, and exporting professional resumes with advanced features.

## Features

### ✨ Core Features
- **User Authentication**: Sign up and login system
- **Resume Creation**: Build comprehensive resumes with personal info, skills, experience, and education
- **Resume Preview**: See how your resume looks before exporting
- **Multiple Export Formats**: Export as PDF or Word document

### 🎨 Theme System
- **4 Professional Themes**: Classic, Modern, Professional, and Creative
- **Dark/Light Mode**: Toggle between dark and light themes
- **Theme Customization**: Choose the perfect theme for your resume

### 🤖 AI Job Finder
- **Skill-Based Matching**: Get job suggestions based on your skills
- **Location Filtering**: Search jobs by location
- **Match Scoring**: See how well you match each job

### 📱 QR Code Generator
- **Instant Access**: Generate QR codes that link to your resume
- **Easy Sharing**: Share your resume with anyone by scanning the QR code
- **Download QR Code**: Save QR code as an image

### 💾 Data Management
- **Local Storage**: All data stored locally in your browser
- **Auto-save**: Resume data is automatically saved
- **Secure**: No data sent to external servers

## Getting Started

### For Users

1. **Access the App**: Visit the GitHub Pages URL (e.g., `https://amremad.github.io/resumebuilder/`)
2. **Create Account**: Sign up with your email and password
3. **Build Resume**: Fill in your personal information, skills, experience, and education
4. **Choose Theme**: Select a theme that matches your style
5. **Preview**: Review your resume before exporting
6. **Export**: Download as PDF or Word document
7. **Generate QR Code**: Create a QR code to share your resume

### For Developers

#### Setup

1. Clone the repository:
```bash
git clone https://github.com/emadamr53/resumebuilder.git
cd resumebuilder/web-app
```

2. Serve the files using a local server:
```bash
# Using Python
python -m http.server 8000

# Using Node.js (http-server)
npx http-server

# Using PHP
php -S localhost:8000
```

3. Open in browser: `http://localhost:8000`

#### Deploy to GitHub Pages

1. Push the `web-app` folder contents to the repository root
2. Go to repository Settings > Pages
3. Select source branch (usually `main`)
4. Select `/ (root)` as the folder
5. Save and wait for deployment

The app will be available at: `https://[username].github.io/resumebuilder/`

## File Structure

```
web-app/
├── index.html          # Main HTML file
├── app.js              # JavaScript application logic
├── styles.css          # CSS styles with theme support
├── manifest.json       # PWA manifest
├── sw.js              # Service worker for offline support
├── README.md          # This file
└── icons/             # App icons (if needed)
```

## Technologies Used

- **HTML5**: Structure
- **CSS3**: Styling with CSS variables for theming
- **JavaScript (ES6+)**: Application logic
- **jsPDF**: PDF generation
- **QRCode.js**: QR code generation
- **Service Worker**: PWA offline support

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## Features in Detail

### Theme System
The app includes 4 professional themes:
- **Classic**: Traditional and timeless design
- **Modern**: Clean and contemporary style
- **Professional**: Corporate and formal layout
- **Creative**: Bold and artistic presentation

### Dark/Light Mode
Toggle between dark and light modes for comfortable viewing in any environment. Your preference is saved automatically.

### QR Code
Generate QR codes that link directly to your resume on GitHub Pages. Perfect for:
- Business cards
- Networking events
- Online portfolios
- Resume sharing

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available for personal and commercial use.

## Support

For issues or questions, please open an issue on GitHub.

---

Made with ❤️ for job seekers everywhere



