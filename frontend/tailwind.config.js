/** @type {import('tailwindcss').Config} */

export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      daisyui: {
        themes: [
          "light",
          "dark",
          "cupcake",
          {
            mytheme: {
              
              "primary": "#ff00ff",
                        
              "secondary": "#ff00ff",
                        
              "accent": "#00ffff",
                        
              "neutral": "#ff00ff",
                        
              "base-100": "#ff00ff",
                        
              "info": "#0000ff",
                        
              "success": "#00ff00",
                        
              "warning": "#00ff00",
                        
              "error": "#ff0000",
            },
          },
        ],
      },
    },
  },
  plugins: [require('daisyui')],
}

