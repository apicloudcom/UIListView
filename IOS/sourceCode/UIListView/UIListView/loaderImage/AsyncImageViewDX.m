//
//  AsyncImageViewDX.m
//  UIListView
//
//  Created by wei on 2019/4/29.
//  Copyright © 2019 zhenhua.liu. All rights reserved.
//

#import "AsyncImageViewDX.h"
#import <CommonCrypto/CommonDigest.h>
@interface AsyncImageViewDX ()
- (void) downloadImage:(NSString *)imageURL;
@end

@implementation AsyncImageViewDX
@synthesize request = _request;
@synthesize needClip = _needClip;

- (void) dealloc {
    self.request.delegate = nil;
    [self cancelDownload];
}

- (void) loadImage:(NSString *)imageURL {
    if (![imageURL isKindOfClass:[NSString class]] || [imageURL length]==0){
        return;
    }
    [self loadImage:imageURL withPlaceholdImage:self.image];
}

- (void) loadImage:(NSString *)imageURL withPlaceholdImage:(UIImage *)placeholdImage {
    self.image = placeholdImage;
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
        UIImage *image = [self getImageInCacheWithURLStr:imageURL];
        dispatch_sync(dispatch_get_main_queue(), ^{
            if (image) {
                self.image = image;
                if (self.needClip) {
                    CGPoint center = self.center;
                    self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, image.size.width, image.size.height);
                    self.center = center;
                }
            } else {
                [self downloadImage:imageURL];
            }
        });
    });
}

- (void) cancelDownload {
    [self.request cancel];
    self.request = nil;
}

#pragma mark -
#pragma mark private downloads
#pragma mark -

- (void) downloadImage:(NSString *)imageURL {
    [self cancelDownload];
    __weak AsyncImageViewDX * asyImg = self;
    NSString * newImageURL = [imageURL stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    self.request = [UZASIHTTPRequest requestWithURL:[NSURL URLWithString:newImageURL]];
    NSString *fileName = [NSString stringWithFormat:@"%@.png",[self md5:imageURL]];
    [self.request setDownloadDestinationPath:[self getImagePathInCache:fileName]];
    [self.request setDelegate:self];
    [self.request setCompletionBlock:^(void){
        asyImg.request.delegate = nil;
        asyImg.request = nil;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            UIImage *image = [asyImg getImageInCacheWithURLStr:imageURL];
            dispatch_sync(dispatch_get_main_queue(), ^{
                if (image) {
                    if (asyImg.needClip) {
                        CGPoint center = asyImg.center;
                        asyImg.frame = CGRectMake(asyImg.frame.origin.x, asyImg.frame.origin.y, image.size.width, image.size.height);
                        asyImg.center = center;
                    }
                    asyImg.alpha = 0;
                    [UIView beginAnimations:nil context:NULL];
                    [UIView setAnimationDuration:0.5];
                    asyImg.image = image;
                    asyImg.alpha = 1.0;
                    [UIView commitAnimations];
                }
                
            });
        });
        
    }];
    [self.request setFailedBlock:^(void){
        [asyImg.request cancel];
        asyImg.request.delegate = nil;
        asyImg.request = nil;
    }];
    [self.request startAsynchronous];
}

- (UIImage*)getImageInCacheWithURLStr:(NSString *)inURLStr{
    UIImage *image = nil;
    NSString *imageName = nil;
    if ((![inURLStr isKindOfClass:[NSString class]]) || ([inURLStr length] == 0)){
        return nil;
    }
    imageName = [NSString stringWithFormat:@"%@.png",[self md5:inURLStr]];
    if ([self imageIsExistInCache:imageName]) {
        image = [UIImage imageWithContentsOfFile:[self getImagePathInCache:imageName]];
    }
    return image;
}

- (NSString *)md5:(NSString *)str{
    const char *cStr = [str UTF8String];
    unsigned char result[16];
    CC_MD5( cStr, (unsigned)strlen(cStr), result );
    return [NSString stringWithFormat:
            @"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
            result[0], result[1], result[2], result[3],
            result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11],
            result[12], result[13], result[14], result[15]
            ];
    
}

- (BOOL)imageIsExistInCache:(NSString*)inImageName {
    NSFileManager *manager = [NSFileManager defaultManager];
    NSString *dir = [NSHomeDirectory() stringByAppendingPathComponent:@"Documents/imageCache"];
    if (![manager fileExistsAtPath:dir]) {
        [manager createDirectoryAtPath:dir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *path = [self getImagePathInCache:inImageName];
    return [manager fileExistsAtPath:path];
}

- (NSString *)getImagePathInCache:(NSString *)inImageName{
    return [NSHomeDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"Documents/imageCache/%@",inImageName]];
}
@end
