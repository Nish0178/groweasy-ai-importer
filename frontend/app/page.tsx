import Navbar from "@/components/common/Navbar";
import Hero from "@/components/common/Hero";
import UploadSection from "@/components/upload/UploadSection";
import HowItWorks from "@/components/common/HowItWorks";
import Stats from "@/components/common/Stats";
import Footer from "@/components/common/Footer";

export default function Home() {
  return (
    <div className="min-h-screen flex flex-col bg-background text-foreground transition-colors duration-300 bg-grid-pattern">
      <Navbar />

      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8 sm:py-12">
          <Hero />
          <UploadSection />
          <HowItWorks />
          <Stats />
        </div>
      </main>

      <Footer />
    </div>
  );
}