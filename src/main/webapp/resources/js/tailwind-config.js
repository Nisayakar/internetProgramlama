tailwind.config = {
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                "on-tertiary-fixed-variant": "#81281b",
                "surface-dim": "#d5dcd7",
                "primary-fixed": "#6dfad2",
                "surface-container-low": "#eef5f0",
                "on-secondary-fixed-variant": "#00504e",
                "outline-variant": "#bbcac3",
                "error-container": "#ffdad6",
                "surface-tint": "#006b55",
                "surface-bright": "#f4fbf6",
                "tertiary": "#a03f30",
                "on-surface": "#161d1a",
                "secondary-fixed-dim": "#2edcd7",
                "on-tertiary": "#ffffff",
                "on-surface-variant": "#3c4a44",
                "surface-container-highest": "#dde4df",
                "background": "#f4fbf6",
                "error": "#ba1a1a",
                "surface-container-high": "#e3eae5",
                "on-primary-fixed-variant": "#005140",
                "tertiary-container": "#f7816d",
                "on-background": "#161d1a",
                "surface-container": "#e8f0eb",
                "secondary": "#006a67",
                "on-error-container": "#93000a",
                "inverse-surface": "#2b322f",
                "tertiary-fixed-dim": "#ffb4a7",
                "primary-fixed-dim": "#4bddb7",
                "inverse-primary": "#4bddb7",
                "on-secondary-fixed": "#00201f",
                "surface-container-lowest": "#ffffff",
                "secondary-fixed": "#5af9f3",
                "on-primary-container": "#004233",
                "on-secondary-container": "#00706e",
                "secondary-container": "#5af9f3",
                "surface": "#f4fbf6",
                "on-tertiary-fixed": "#400200",
                "surface-variant": "#dde4df",
                "on-tertiary-container": "#6e1b0f",
                "tertiary-fixed": "#ffdad4",
                "inverse-on-surface": "#ebf2ee",
                "on-error": "#ffffff",
                "primary-container": "#00b894",
                "on-secondary": "#ffffff",
                "outline": "#6c7a74",
                "on-primary-fixed": "#002018",
                "on-primary": "#ffffff",
                "primary": "#006b55"
            },
            borderRadius: {
                "DEFAULT": "0.25rem",
                "lg": "0.5rem",
                "xl": "0.75rem",
                "full": "9999px"
            },
            spacing: {
                "gutter": "24px",
                "base-unit": "8px",
                "container-max-width": "1280px",
                "section-padding": "80px",
                "margin": "32px"
            },
            fontFamily: {
                "body-md": ["Inter"],
                "label-sm": ["Inter"],
                "h3": ["Inter"],
                "h2": ["Inter"],
                "h1": ["Inter"],
                "body-lg": ["Inter"]
            },
            fontSize: {
                "body-md": ["16px", { lineHeight: "1.6", fontWeight: "400" }],
                "label-sm": ["14px", { lineHeight: "1.4", letterSpacing: "0.01em", fontWeight: "500" }],
                "h3": ["24px", { lineHeight: "1.3", fontWeight: "600" }],
                "h2": ["36px", { lineHeight: "1.2", letterSpacing: "-0.01em", fontWeight: "700" }],
                "h1": ["48px", { lineHeight: "1.2", letterSpacing: "-0.02em", fontWeight: "700" }],
                "body-lg": ["18px", { lineHeight: "1.6", fontWeight: "400" }]
            }
        }
    }
};
