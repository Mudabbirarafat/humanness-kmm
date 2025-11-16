import Foundation

extension Notification.Name {
    static let tasksUpdated = Notification.Name("tasksUpdated")
}

struct StoredTask: Codable {
    let id: String
    let task_type: String
    let text: String?
    let image_url: String?
    let image_path: String?
    let audio_path: String?
    let duration_sec: Int
    let timestamp: String
}

enum FileStorage {
    static private func tasksFileURL() -> URL {
        let fm = FileManager.default
        let docs = fm.urls(for: .documentDirectory, in: .userDomainMask).first!
        return docs.appendingPathComponent("tasks.json")
    }

    static func loadTasks() -> [StoredTask] {
        let file = tasksFileURL()
        let fm = FileManager.default
        guard fm.fileExists(atPath: file.path) else { return [] }
        do {
            let data = try Data(contentsOf: file)
            let dec = try JSONDecoder().decode([StoredTask].self, from: data)
            return dec
        } catch {
            print("FileStorage.loadTasks error:", error)
            return []
        }
    }

    static func saveTasks(_ tasks: [StoredTask]) {
        let file = tasksFileURL()
        do {
            let data = try JSONEncoder().encode(tasks)
            try data.write(to: file)
            NotificationCenter.default.post(name: .tasksUpdated, object: nil)
        } catch {
            print("FileStorage.saveTasks error:", error)
        }
    }

    static func addTask(_ t: StoredTask) {
        var list = loadTasks()
        list.insert(t, at: 0)
        saveTasks(list)
    }
}
