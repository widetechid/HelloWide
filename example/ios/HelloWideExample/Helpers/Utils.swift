//
//  Utils.swift
//  HelloWideExample
//
//  Created by Wijaya, Ardy on 03/09/22.
//

import Foundation
import UIKit

extension UIView {
    
    func showToast(message: String) {
        DispatchQueue.main.async {
            let toastLabel = UILabel(frame: CGRect(x: self.frame.width/2 - 100, y: self.frame.height-100, width: 200, height: 40))
            toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
            toastLabel.textColor = UIColor.white
            toastLabel.font = UIFont.systemFont(ofSize: 16)
            toastLabel.textAlignment = .center;
            toastLabel.text = message
            toastLabel.alpha = 1.0
            toastLabel.layer.cornerRadius = 5;
            toastLabel.clipsToBounds  =  true
            self.addSubview(toastLabel)
            UIView.animate(withDuration: 2.0, delay: 0.1, options: .curveEaseOut, animations: {
                toastLabel.alpha = 0.0
                }, completion: {(isCompleted) in
                    toastLabel.removeFromSuperview()
            })
        }
        
    }

}
