//
//  ViewController.swift
//  HelloWideExample
//
//  Created by Wijaya, Ardy on 01/09/22.
//

import UIKit
import HelloWide

class ViewController: UIViewController {
    var voipTokenTextView = UITextView()

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        let titleLabel = UILabel()
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.text = "HelloWide Demo"
        titleLabel.textColor = UIColor.black
        titleLabel.textAlignment = .center
        titleLabel.font = UIFont.systemFont(ofSize: 24.0)
        self.view.addSubview(titleLabel)
        
        titleLabel.topAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.topAnchor, constant: 30.0).isActive = true
        titleLabel.centerXAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.centerXAnchor).isActive = true
        titleLabel.widthAnchor.constraint(equalToConstant: self.view.safeAreaLayoutGuide.layoutFrame.width - 100.0).isActive = true
        titleLabel.heightAnchor.constraint(equalToConstant: 50.0).isActive = true
        
        voipTokenTextView.translatesAutoresizingMaskIntoConstraints = false
        voipTokenTextView.textColor = UIColor.red
        voipTokenTextView.textAlignment = .center
        voipTokenTextView.layer.borderWidth = 1.0
        voipTokenTextView.layer.borderColor = UIColor.black.cgColor
        self.view.addSubview(voipTokenTextView)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.receivePushKitToken(_:)), name: NSNotification.Name(rawValue:"receivePushKitToken"), object: nil)
        
        voipTokenTextView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 20.0).isActive = true
        voipTokenTextView.centerXAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.centerXAnchor).isActive = true
        voipTokenTextView.widthAnchor.constraint(equalToConstant: self.view.safeAreaLayoutGuide.layoutFrame.width - 100.0).isActive = true
        voipTokenTextView.heightAnchor.constraint(equalToConstant: 50.0).isActive = true
        
        let copyLabel = UILabel()
        copyLabel.translatesAutoresizingMaskIntoConstraints = false
        copyLabel.text = "Copy this token"
        copyLabel.textColor = UIColor.systemBlue
        copyLabel.textAlignment = .center
        copyLabel.isUserInteractionEnabled = true
        copyLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(copyToken)))
        self.view.addSubview(copyLabel)
        
        copyLabel.topAnchor.constraint(equalTo: voipTokenTextView.bottomAnchor, constant: 10.0).isActive = true
        copyLabel.centerXAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.centerXAnchor).isActive = true
        copyLabel.widthAnchor.constraint(equalToConstant: 200.0).isActive = true
        copyLabel.heightAnchor.constraint(equalToConstant: 50.0).isActive = true
        
        let voiceCallButton = UIButton()
        voiceCallButton.translatesAutoresizingMaskIntoConstraints = false
        voiceCallButton.tag = 1
        voiceCallButton.backgroundColor =  UIColor.init(red: 51.0/255.0, green: 181.0/255.0, blue: 229.0/255.0, alpha: 1.0)
        voiceCallButton.layer.cornerRadius = 30.0
        voiceCallButton.setImage(UIImage.init(named: "ic_call_cs.png"), for: UIControl.State.normal)
        voiceCallButton.tintColor = UIColor.white
        voiceCallButton.addTarget(self, action: #selector(onCallButton), for: UIControl.Event.touchUpInside)
        self.view.addSubview(voiceCallButton)
        
        voiceCallButton.leadingAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.leadingAnchor, constant: 20.0).isActive = true
        voiceCallButton.bottomAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.bottomAnchor, constant: -20.0).isActive = true
        voiceCallButton.heightAnchor.constraint(equalToConstant: 60.0).isActive = true
        voiceCallButton.widthAnchor.constraint(equalToConstant: 60.0).isActive = true

        let videoCallButton = UIButton()
        videoCallButton.translatesAutoresizingMaskIntoConstraints = false
        videoCallButton.tag = 2
        videoCallButton.backgroundColor =  UIColor.init(red: 51.0/255.0, green: 181.0/255.0, blue: 229.0/255.0, alpha: 1.0)
        videoCallButton.layer.cornerRadius = 30.0
        videoCallButton.setImage(UIImage.init(named: "ic_video_call_cs.png"), for: UIControl.State.normal)
        videoCallButton.tintColor = UIColor.white
        videoCallButton.addTarget(self, action: #selector(onCallButton), for: UIControl.Event.touchUpInside)
        self.view.addSubview(videoCallButton)
        
        videoCallButton.trailingAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.trailingAnchor, constant: -20.0).isActive = true
        videoCallButton.bottomAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.bottomAnchor, constant: -20.0).isActive = true
        videoCallButton.heightAnchor.constraint(equalToConstant: 60.0).isActive = true
        videoCallButton.widthAnchor.constraint(equalToConstant: 60.0).isActive = true
    }
    
    @objc func receivePushKitToken(_ notification: NSNotification) {
        voipTokenTextView.text = UserDefaults.standard.object(forKey: HWCONSTANTS_PUSHKIT_TOKEN) as? String ?? ""
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: "receivePushKitToken"), object: nil)
    }
    
    @objc func copyToken() {
        UIPasteboard.general.string = voipTokenTextView.text
        self.view.showToast(message: "Copied To Clipboard")
    }
    
    @objc func onCallButton(sender: UIButton) {
        if (sender.tag == 1) {
            HWCallCompact.sharedInstance().processOutgoingCall(withCallType: HWCONSTANTS_CALL_TYPE_VOICE)
        }else {
            HWCallCompact.sharedInstance().processOutgoingCall(withCallType: HWCONSTANTS_CALL_TYPE_VIDEO)
        }
    }
    
}
