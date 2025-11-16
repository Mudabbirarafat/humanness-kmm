import SwiftUI

struct Task: Codable, Identifiable {
    let id: String
    let task_type: String
    let text: String?
    let image_url: String?
    let image_path: String?
    let audio_path: String?
    let duration_sec: Int
    let timestamp: String
}

class TaskStore: ObservableObject {
    @Published var tasks: [Task] = []

    func load() {
        let fm = FileManager.default
        let docs = fm.urls(for: .documentDirectory, in: .userDomainMask).first!
        let file = docs.appendingPathComponent("tasks.json")
        guard fm.fileExists(atPath: file.path) else {
            tasks = []
            return
        }
        do {
            let data = try Data(contentsOf: file)
            let dec = try JSONDecoder().decode([Task].self, from: data)
            DispatchQueue.main.async { self.tasks = dec }
        } catch {
            print("Failed to load tasks.json:", error)
            tasks = []
        }
    }

    init() {
        NotificationCenter.default.addObserver(forName: .tasksUpdated, object: nil, queue: .main) { [weak self] _ in
            self?.load()
        }
    }
}

struct ContentView: View {
    @StateObject private var store = TaskStore()

    init() {
        // If you embed the shared Kotlin framework, call PlatformStorage.shared.setBasePath(...) here.
        // Example (after adding `shared.framework` to the project):
        // import shared
        // let docs = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first!
        // PlatformStorage.shared.setBasePath(path: docs)
    }

    var body: some View {
        NavigationView {
            VStack {
                Text("Humanness — Task History")
                    .font(.headline)
                    .padding(.top)

                // Quick access controls for testing: record and capture
                HStack(spacing: 16) {
                    RecordControls()
                    Button(action: {
                        // present camera
                    }) {
                        Text("Capture Photo")
                            .padding(10)
                            .background(Color.green)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }.padding()

                HStack {
                    Text("Total Tasks: \(store.tasks.count)")
                    Spacer()
                    let total = store.tasks.reduce(0) { $0 + $1.duration_sec }
                    Text("Total Duration: \(total)s")
                }.padding()

                List(store.tasks) { t in
                    VStack(alignment: .leading, spacing: 4) {
                        Text("\(t.task_type) — \(t.duration_sec)s").font(.subheadline).bold()
                        Text(t.timestamp).font(.caption)
                        if let text = t.text, !text.isEmpty { Text(text).font(.body) }
                        if let img = t.image_url ?? t.image_path { Text("Preview: \(img)").font(.caption) }
                    }.padding(6)
                }
            }
            .navigationBarTitle("Task History", displayMode: .inline)
            .onAppear { store.load() }
        }
    }
}
