import SwiftUI

struct RecordControls: View {
    @State private var recording = false
    @State private var lastPath: String = ""
    @State private var lastDuration: Int = 0

    var body: some View {
        VStack(spacing: 12) {
            Text(recording ? "Recording..." : "Press to record")
                .font(.headline)

            Button(action: {
                if recording {
                    if let res = SharedBridge.stopRecording() {
                        lastPath = res.path ?? ""
                        lastDuration = Int(res.durationSec)

                        // create a StoredTask and append to tasks.json
                        let t = StoredTask(id: UUID().uuidString,
                                           task_type: "text_reading",
                                           text: "",
                                           image_url: nil,
                                           image_path: nil,
                                           audio_path: lastPath,
                                           duration_sec: lastDuration,
                                           timestamp: ISO8601DateFormatter().string(from: Date()))
                        FileStorage.addTask(t)
                    }
                    recording = false
                } else {
                    // ensure base path is set
                    SharedBridge.setBasePathToDocuments()
                    let ok = SharedBridge.startRecording()
                    if ok { recording = true }
                }
            }) {
                Text(recording ? "Stop" : "Start Recording")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }

            if !lastPath.isEmpty {
                Text("Saved: \(lastPath)")
                Text("Duration: \(lastDuration)s")
            }
        }
        .padding()
    }
}

struct RecordControls_Previews: PreviewProvider {
    static var previews: some View {
        RecordControls()
    }
}
