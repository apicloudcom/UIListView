//
//  AsyncImageViewDX.h
//  UIListView
//
//  Created by wei on 2019/4/29.
//  Copyright © 2019 zhenhua.liu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "UZASIHTTPRequest.h"
#import <CommonCrypto/CommonDigest.h>
@interface AsyncImageViewDX : UIImageView
<ASIHTTPRequestDelegate>

@property (nonatomic,retain) UZASIHTTPRequest *request;
@property (nonatomic) BOOL needClip;

- (void) loadImage:(NSString *)imageURL;
- (void) loadImage:(NSString *)imageURL withPlaceholdImage:(UIImage *)image;
- (void) cancelDownload;

@end
