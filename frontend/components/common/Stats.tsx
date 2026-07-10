const stats = [
  {
    number: "100%",
    label: "AI Powered Mapping",
  },
  {
    number: "15+",
    label: "CRM Fields Extracted",
  },
  {
    number: "Any",
    label: "CSV Format Supported",
  },
  {
    number: "Fast",
    label: "Batch Processing",
  },
];

export default function Stats() {
  return (
    <section className="mt-24">
      <div className="grid gap-6 rounded-3xl bg-slate-900 p-10 text-center text-white md:grid-cols-4">
        {stats.map((item) => (
          <div key={item.label}>
            <h3 className="text-4xl font-bold">{item.number}</h3>
            <p className="mt-2 text-slate-300">{item.label}</p>
          </div>
        ))}
      </div>
    </section>
  );
}