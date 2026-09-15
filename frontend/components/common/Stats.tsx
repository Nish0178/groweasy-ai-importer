import { Sparkles, Shield, Cpu, Zap } from "lucide-react";

const stats = [
  {
    number: "100%",
    label: "Automated Field Mapping",
    description: "Zero manual column renaming",
    icon: Sparkles,
  },
  {
    number: "15+",
    label: "Standard CRM Fields",
    description: "Normalized & validated schema",
    icon: Cpu,
  },
  {
    number: "< 3s",
    label: "Batch Processing Time",
    description: "Instantaneous AI classification",
    icon: Zap,
  },
  {
    number: "100%",
    label: "Data Privacy & Security",
    description: "Immediate disk cleanup on parse",
    icon: Shield,
  },
];

export default function Stats() {
  return (
    <section id="stats" className="mt-28 scroll-mt-24">
      <div className="relative overflow-hidden rounded-3xl border border-slate-200/90 bg-gradient-to-b from-blue-50/80 via-white to-indigo-50/50 p-8 sm:p-12 text-slate-900 shadow-xl transition-colors dark:border-slate-800 dark:bg-gradient-to-b dark:from-slate-900 dark:via-slate-900 dark:to-indigo-950 dark:text-white dark:shadow-2xl">
        {/* Glow decoration */}
        <div className="pointer-events-none absolute -top-24 -right-24 h-64 w-64 rounded-full bg-blue-500/10 dark:bg-blue-500/20 blur-3xl" />
        <div className="pointer-events-none absolute -bottom-24 -left-24 h-64 w-64 rounded-full bg-purple-500/10 dark:bg-purple-500/20 blur-3xl" />

        <div className="relative grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
          {stats.map((item) => {
            const Icon = item.icon;
            return (
              <div
                key={item.label}
                className="flex flex-col items-center text-center p-5 rounded-2xl bg-white/80 dark:bg-white/5 backdrop-blur-xs border border-slate-200/70 dark:border-white/10 shadow-xs transition hover:-translate-y-1 hover:shadow-md"
              >
                <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-blue-100 text-blue-600 dark:bg-blue-500/20 dark:text-blue-400 mb-4">
                  <Icon className="h-5 w-5" />
                </div>
                <h3 className="text-3xl sm:text-4xl font-extrabold tracking-tight bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent dark:from-white dark:via-slate-100 dark:to-slate-300">
                  {item.number}
                </h3>
                <p className="mt-2 text-sm font-semibold text-slate-800 dark:text-slate-200">
                  {item.label}
                </p>
                <p className="mt-1 text-xs text-slate-500 dark:text-slate-400">
                  {item.description}
                </p>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}