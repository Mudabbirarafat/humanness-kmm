import SwiftUI
import UIKit

struct PhotoCaptureView: UIViewControllerRepresentable {
    @Environment(\.presentationMode) var presentationMode
    var onImageSaved: ((String) -> Void)?

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .camera
        picker.cameraCaptureMode = .photo
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator { Coordinator(self) }

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: PhotoCaptureView
        init(_ parent: PhotoCaptureView) { self.parent = parent }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let image = info[.originalImage] as? UIImage {
                // save to documents
                if let data = image.jpegData(compressionQuality: 0.9) {
                    let fm = FileManager.default
                    let docs = fm.urls(for: .documentDirectory, in: .userDomainMask).first!
                    let filename = "photo_\(Int(Date().timeIntervalSince1970)).jpg"
                    let file = docs.appendingPathComponent(filename)
                    do {
                        try data.write(to: file)
                        parent.onImageSaved?(file.path)
                        // also append a task entry
                        let t = StoredTask(id: UUID().uuidString,
                                           task_type: "photo_capture",
                                           text: parent.description,
                                           image_url: nil,
                                           image_path: file.path,
                                           audio_path: nil,
                                           duration_sec: 0,
                                           timestamp: ISO8601DateFormatter().string(from: Date()))
                        FileStorage.addTask(t)
                    } catch {
                        print("Failed to save image:", error)
                    }
                }
            }
            parent.presentationMode.wrappedValue.dismiss()
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.presentationMode.wrappedValue.dismiss()
        }
    }
}

struct PhotoCaptureView_Previews: PreviewProvider {
    static var previews: some View { Text("Photo Capture") }
}
