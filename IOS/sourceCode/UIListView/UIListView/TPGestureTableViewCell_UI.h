//
//  TPGestureTableViewCell.h
//  UIListView
//
//  Created by Anonymity on ？？-6-28.
//  Copyright (c) 20？？年 ？？. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
typedef enum {
    kFeedStatusNormal = 0,
    kFeedStatusLeftExpanded,
    kFeedStatusLeftExpanding,
    kFeedStatusRightExpanded,
    kFeedStatusRightExpanding,
}kFeedStatus;

@class TPGestureTableViewCell_UI;

@protocol TPGestureTableViewCellDelegate <NSObject>
@optional
- (NSString *)getRealPath:(NSString *)path;
- (void)cellDidSelectArrow:(TPGestureTableViewCell_UI *)cell;
- (void)cellDidSelectAvatar:(TPGestureTableViewCell_UI *)cell;
- (void)cellDidBeginPan:(TPGestureTableViewCell_UI *)cell;  
- (void)cellDidReveal:(TPGestureTableViewCell_UI *)cell;
- (void)cellDidSelectLeftBtn:(TPGestureTableViewCell_UI *)cell andTag:(NSInteger)tag;
- (void)cellDidSelectRightBtn:(TPGestureTableViewCell_UI *)cell andTag:(NSInteger)tag;

@end

@interface TPGestureTableViewCell_UI : UITableViewCell<UIGestureRecognizerDelegate>

@property (nonatomic, assign) id<TPGestureTableViewCellDelegate> delegate;
@property (nonatomic, assign) kFeedStatus currentStatusUI;
@property (nonatomic, assign) BOOL revealingUI, forbiddenClick;
@property (nonatomic,assign) float slipDistanceUI;
@property (nonatomic,assign) float slipLeftDistance;
@property (nonatomic, copy) NSArray *leftBtnUI;//左边的按钮
@property (nonatomic, copy) NSArray *rightBtn;//右边的按钮
@property (nonatomic, retain) UIImageView *iconImg;
@property (nonatomic, retain) UILabel *titleLabel;
@property (nonatomic, retain) UILabel *detailTextViewUI;
@property (nonatomic, assign) NSInteger cellIndex;
@property (nonatomic, retain) NSString *leftCellBgColor;
@property (nonatomic, retain) NSString *rightCellBgColor;
@property (nonatomic, retain) UILabel *remark;
@property (nonatomic, retain) UIImageView *arrow;
@property (nonatomic, strong) UIButton *arrowBtn;
@property (nonatomic , assign) CGFloat originalCenter;
@property (nonatomic , strong) UIView *botLine;


- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier andBorderColor:(NSString *)borderColor andHeight:(float)cellHeight withImgSize:(CGSize)size withMarkStyle:(NSDictionary *)markStyle borderWidth:(float)borderWidth;

@end
