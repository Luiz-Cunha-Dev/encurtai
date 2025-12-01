import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  webpack: (config) => {
    if (process.env.NODE_ENV === "development") {
      config.module.rules.push({
        test: /\.(jsx|tsx)$/,
        exclude: /node_modules/,
        enforce: "pre",
        use: "@dyad-sh/nextjs-webpack-component-tagger",
      });
    }
    return config;
  },
  async rewrites() {
    return [
      {
        source: '/api/sms/:path*',
        destination: 'http://localhost:8000/sms/:path*',
      },
      {
        source: '/api/sms',
        destination: 'http://localhost:8000/sms',
      },
      {
        source: '/api/rs/:path*',
        destination: 'http://localhost:8000/rs/:path*',
      },
      {
        source: '/api/smts/:path*',
        destination: 'http://localhost:8000/smts/:path*',
      },
    ];
  },
};

export default nextConfig;
