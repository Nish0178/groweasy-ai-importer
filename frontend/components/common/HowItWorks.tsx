import { Upload, BrainCircuit, DatabaseZap, ArrowRight } from "lucide-react";

const steps = [
  {
    step: "01",
    icon: Upload,
    title: "Upload Any CSV",
    description:
      "Upload leads from Facebook, Google Ads, HubSpot, Excel, or custom CRM dumps without preparing or cleaning columns first.",
  },
  {
    step: "02",
    icon: BrainCircuit,
    title: "AI Maps & Standardizes",
    description:
      "Gemini AI analyzes every column header, normalizes phone numbers, handles multiple emails, and matches GrowEasy CRM schema.",
  },
  {
    step: "03",
    icon: DatabaseZap,
    title: "Export Clean Pipeline",
    description:
      "Instantly download formatted Excel spreadsheets or push clean leads straight into your CRM without manual data entry.",
  },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="mt-28 scroll-mt-24">
      <div className="text-center">
        <div className="inline-flex items-center gap-1.5 rounded-full border border-blue-200/80 bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-700 dark:border-blue-900/60 dark:bg-blue-950/60 dark:text-blue-300">
          <span>Workflow Automation</span>
        </div>
        <h2 className="mt-4 text-3xl sm:text-4xl font-bold tracking-tight text-slate-900 dark:text-white">
          How It Works
        </h2>
        <p className="mt-3 text-sm sm:text-base text-slate-500 dark:text-slate-400 max-w-xl mx-auto">
          Import and normalize thousands of leads in three simple, fully automated steps.
        </p>
      </div>

      <div className="mt-14 grid gap-8 md:grid-cols-3">
        {steps.map((step) => {
          const Icon = step.icon;

          return (
            <div
              key={step.step}
              className="group relative rounded-3xl border border-slate-200/90 bg-white/95 p-8 shadow-sm transition-all duration-300 hover:-translate-y-1.5 hover:shadow-xl dark:border-slate-800 dark:bg-slate-900/90"
            >
              <div className="flex items-center justify-between">
                <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-50 text-blue-600 transition-colors group-hover:bg-blue-600 group-hover:text-white dark:bg-blue-950/80 dark:text-blue-400 dark:group-hover:bg-blue-600 dark:group-hover:text-white">
                  <Icon className="h-7 w-7" />
                </div>
                <span className="font-mono text-2xl font-black text-slate-200 dark:text-slate-800">
                  {step.step}
                </span>
              </div>

              <h3 className="mt-6 text-xl font-bold text-slate-900 dark:text-white">
                {step.title}
              </h3>

              <p className="mt-3 text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
                {step.description}
              </p>
            </div>
          );
        })}
      </div>
    </section>
  );
}
