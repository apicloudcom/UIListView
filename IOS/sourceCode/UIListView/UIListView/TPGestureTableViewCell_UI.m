//
//  TPGestureTableViewCell.m
//  UIListView
//
//  Created by Anonymity on ？？-6-28.
//  Copyright (c) 20？？年 ？？. All rights reserved.
//

#import "TPGestureTableViewCell_UI.h"
#import <QuartzCore/QuartzCore.h>
#import "NSDictionaryUtils.h"
#import "UZAppUtils.h"

@interface SeperateLineUI : UIView

@property (nonatomic,retain) NSString *colors;

@end

@implementation SeperateLineUI
@synthesize colors;

#pragma mark-
#pragma mark SeperateLine
#pragma mark-

- (void)dealloc{
    self.colors = nil;
    [super dealloc ];
}

@end

#define kMinimumVelocity  self.contentView.frame.size.width*1.5
#define kMinimumPan       60.0
#define kBOUNCE_DISTANCE  7.0

typedef enum {
    LMFeedCellDirectionNone=0,
	LMFeedCellDirectionRight,
	LMFeedCellDirectionLeft,
} LMFeedCellDirection;

@interface TPGestureTableViewCell_UI ()

//flag
@property (nonatomic, retain) UIPanGestureRecognizer *panGesture;
@property (nonatomic, assign) CGFloat initialHorizontalCenter;
@property (nonatomic, assign) CGFloat initialTouchPositionX;
@property (nonatomic, assign) LMFeedCellDirection lastDirection;
@property (nonatomic, retain) SeperateLineUI *seperateLineUI;
@property (nonatomic, retain) UIView *bottomRightView;
@property (nonatomic, retain) UIView *bottomLeftView;

@end

@implementation TPGestureTableViewCell_UI

@synthesize delegate;
@synthesize initialHorizontalCenter = _initialHorizontalCenter;
@synthesize initialTouchPositionX = _initialTouchPositionX;
@synthesize bottomLeftView = _bottomLeftView;
@synthesize bottomRightView = _bottomRightView;
@synthesize seperateLineUI = _seperateLineUI;
@synthesize leftBtnUI, rightBtn;
@synthesize iconImg;
@synthesize cellIndex;
@synthesize leftCellBgColor, rightCellBgColor;
@synthesize remark = _remark, arrow = _arrow;
@synthesize slipDistanceUI, slipLeftDistance;
@synthesize botLine = _botLine;

#pragma mark-
#pragma mark lifeCycle
#pragma mark-

- (void)dealloc {
    self.leftBtnUI = nil;
    self.rightBtn = nil;
    self.leftCellBgColor = nil;
    self.rightCellBgColor = nil;
    self.iconImg = nil;
    self.panGesture = nil;
    if (_detailTextViewUI) {
        [_detailTextViewUI removeFromSuperview];
        self.detailTextViewUI = nil;
    }
    if (_seperateLineUI) {
        [_seperateLineUI removeFromSuperview];
        self.seperateLineUI = nil;
    }
    if (_titleLabel) {
        [_titleLabel removeFromSuperview];
        self.titleLabel = nil;
    }
    if (_remark) {
        [_remark removeFromSuperview];
        self.remark = nil;
    }
    if (_arrow) {
        [_arrow removeFromSuperview];
        self.arrow = nil;
    }
    if (_bottomRightView) {
        [_bottomRightView removeFromSuperview];
        self.bottomRightView = nil;
    }
    if (_bottomLeftView) {
        [_bottomLeftView removeFromSuperview];
        self.bottomLeftView = nil;
    }
    if (_botLine) {
        [_botLine removeFromSuperview];
        self.botLine = nil;
    }
    [super dealloc];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier andBorderColor:(NSString*)borderColor andHeight:(float)cellHeight withImgSize:(CGSize)size withMarkStyle:(NSDictionary *)markStyle borderWidth:(float)borderWidth{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        _currentStatusUI=kFeedStatusNormal;
        //头像
        float iconHeight = size.height;
        float iconWidth = size.width;
        if (iconHeight == 0 || iconHeight>cellHeight) {
            iconHeight = cellHeight - 20;
        }
        if (iconWidth == 0) {
            iconWidth = cellHeight - 20;
        }
        float icony = (cellHeight - iconHeight)/2.0;
        float iconx = icony;
        if (iconWidth>cellHeight || iconx>15) {
            iconx = 10;
        }
       AsyncImageViewDX  *iconView=[[AsyncImageViewDX alloc]initWithFrame:CGRectMake(iconx, icony,iconWidth, iconHeight)];
//        UIImageView  *iconView=[[UIImageView alloc]initWithFrame:CGRectMake(iconx, icony,iconWidth, iconHeight)];
        iconView.clipsToBounds = YES;
        self.iconImg=iconView;
        [self.contentView addSubview:self.iconImg];
        [iconView release];
        iconView.userInteractionEnabled = YES;
        
        //头像点击事件
        UIButton *cellBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        cellBtn.frame = self.iconImg.frame;
        [cellBtn addTarget:self action:@selector(tapHandle:) forControlEvents:UIControlEventTouchDown];
        cellBtn.backgroundColor = [UIColor clearColor];
        [iconImg addSubview:cellBtn];
        
        //右边图标
        NSString *markColor = nil;
        float margin, remarkSize, arrowSize;
        if (markStyle) {
            markColor = [markStyle stringValueForKey:@"remarkColor" defaultValue:@"#000000"];
            if (!markColor.length) {
                markColor = @"#000000";
            }
            margin = [markStyle floatValueForKey:@"remarkMargin" defaultValue:10];
            remarkSize = [markStyle floatValueForKey:@"remarkSize" defaultValue:16];
            arrowSize = [markStyle floatValueForKey:@"arrowSize" defaultValue:30];
        } else {
            markColor = @"#000000";
            margin = 10;
            remarkSize = 16;
            arrowSize = 30;
        }
        
        //标题
        _titleLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        _titleLabel.backgroundColor = [UIColor clearColor];
        _titleLabel.frame = CGRectMake(iconWidth+iconx*2,
                                       icony,
                                       self.frame.size.width-iconView.bounds.size.width-10-10,
                                       iconView.bounds.size.height/2);
        
        _titleLabel.textColor = [UIColor redColor];
        _titleLabel.font = [UIFont systemFontOfSize:15];
        _titleLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:_titleLabel];
        
        //子标题
        _detailTextViewUI = [[UILabel alloc]initWithFrame:CGRectZero];
        _detailTextViewUI.backgroundColor = [UIColor clearColor];
        _detailTextViewUI.textColor = [UIColor blackColor];
        _detailTextViewUI.font = [UIFont systemFontOfSize:13];
        _detailTextViewUI.numberOfLines = 0;
        _detailTextViewUI.frame = CGRectMake(iconView.frame.size.width+10+10+iconx,
                                           _titleLabel.frame.origin.y+_titleLabel.frame.size.height,
                                           self.bounds.size.width-iconView.bounds.size.width-10-10,
                                           iconView.bounds.size.height/2);
        _detailTextViewUI.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:_detailTextViewUI];
        
        //分割线
        _seperateLineUI = [[SeperateLineUI alloc]initWithFrame:CGRectZero];
        _seperateLineUI.colors = borderColor;
        _seperateLineUI.frame=CGRectMake(-self.frame.size.width, 0, self.bounds.size.width*10,borderWidth/2.0);
        _seperateLineUI.backgroundColor = [UZAppUtils colorFromNSString:borderColor];
        [self.contentView addSubview:_seperateLineUI];
        _originalCenter= [UIScreen mainScreen].bounds.size.width / 2.0;
        //下分割线
        _botLine = [[UIView alloc]initWithFrame:CGRectZero];
        _botLine.backgroundColor = [UZAppUtils colorFromNSString:borderColor];
        _botLine.frame = CGRectMake(-self.frame.size.width, cellHeight-borderWidth/2.0, self.bounds.size.width*10,borderWidth/2.0);
        _botLine.hidden = YES;
        [self.contentView addSubview:_botLine];
        //self.separatorInset = UIEdgeInsetsMake(borderWidth/2.0, 0, borderWidth/2.0, 0);
        
        //添加手势事件
        _panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panGestureHandle:)];
		_panGesture.delegate = self;
        [self addGestureRecognizer:_panGesture];
        
        //右边图标
        _arrow = [[UIImageView alloc]initWithFrame:CGRectZero];
        _arrow.frame = CGRectMake(self.frame.size.width-10-arrowSize, (cellHeight-arrowSize)/2.0, arrowSize, arrowSize);
        _remark.frame = CGRectMake(0, 0, self.frame.size.width, cellHeight);
        _arrow.backgroundColor = [UIColor clearColor];
        [self.contentView addSubview:_arrow];
        _arrow.userInteractionEnabled = YES;
        
        //右边图标点击事件
        _arrowBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _arrowBtn.frame = self.arrow.bounds;
        [_arrowBtn addTarget:self action:@selector(tapHandleArrowBtn:) forControlEvents:UIControlEventTouchDown];
        _arrowBtn.backgroundColor = [UIColor clearColor];
        [self.contentView addSubview:_arrowBtn];
        
        //备注
        _remark = [[UILabel alloc]initWithFrame:CGRectZero];
        _remark.backgroundColor = [UIColor clearColor];
        _remark.textColor = [UZAppUtils colorFromNSString:markColor];
        _remark.font = [UIFont systemFontOfSize:remarkSize];
        _remark.textAlignment = NSTextAlignmentRight;
        _remark.frame = CGRectMake(0, 0, self.frame.size.width-10, cellHeight);
        [self.contentView addSubview:_remark];
    }
    return self;
}

- (void)layoutSubviews{
    [super layoutSubviews];
}

#pragma mark-
#pragma mark gesturMethod
#pragma mark-

-(void)layoutBottomView{
    if(!self.bottomRightView){
        _bottomRightView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height)];
        _bottomRightView.backgroundColor = [UZAppUtils colorFromNSString:self.rightCellBgColor];
        if (self.rightBtn) {
            float width = 0;
            for (int i = (int)rightBtn.count - 1; i >= 0; i--) {
                NSDictionary *btnInfo = [rightBtn objectAtIndex:i];
                UIButton *cellBtn = [UIButton buttonWithType:UIButtonTypeCustom];
                float x;
                float w = [btnInfo floatValueForKey:@"width" defaultValue:self.frame.size.width / 4];
                width += w;
                x = _bottomRightView.frame.size.width - width ;
                cellBtn.frame = CGRectMake(x, 0, w, _bottomRightView.frame.size.height);
                [cellBtn addTarget:self action:@selector(didSelectRightBtn:) forControlEvents:UIControlEventTouchUpInside];
                NSString *bgStr = [btnInfo stringValueForKey:@"bgColor" defaultValue:@"#388e8e"];
                if ([UZAppUtils isValidColor:bgStr]) {
                    cellBtn.backgroundColor = [UZAppUtils colorFromNSString:bgStr];
                } else {
                    UIImage *image = [UIImage imageWithContentsOfFile:[self.delegate getRealPath:bgStr]];
                    [cellBtn setBackgroundImage:image forState:UIControlStateNormal];
                }
                NSString *selectNoColor = [btnInfo stringValueForKey:@"activeBgColor" defaultValue:bgStr];
                UIImage *selectImg;
                if ([UZAppUtils isValidColor:selectNoColor]) {
                    selectImg = [self createImageWithColor:[UZAppUtils colorFromNSString:selectNoColor]];
                } else {
                    selectImg = [UIImage imageWithContentsOfFile:[self.delegate getRealPath:selectNoColor]];
                }
                [cellBtn setBackgroundImage:selectImg forState:UIControlStateHighlighted];
                cellBtn.tag = i;
                //按钮标题icon
                NSString *titleStr = [btnInfo stringValueForKey:@"title" defaultValue:@""];
                float titleSize = [btnInfo floatValueForKey:@"titleSize" defaultValue:12];
                CGSize constraintSize;
                constraintSize.width = cellBtn.frame.size.width;
                CGSize strSize = [titleStr sizeWithFont:[UIFont systemFontOfSize:titleSize]
                                      constrainedToSize:constraintSize
                                          lineBreakMode:NSLineBreakByWordWrapping];
                float iconWidth = [btnInfo floatValueForKey:@"iconWidth" defaultValue:20];
                NSString *icontStr = [btnInfo stringValueForKey:@"icon" defaultValue:nil];
                if (icontStr.length == 0) {
                    iconWidth = 0;
                }
                float totalW = iconWidth + 5 + strSize.width;
                float widthSpace = (w - totalW)/2.0;
                float heightSpace = (cellBtn.frame.size.height - iconWidth)/2.0;
                if (icontStr.length > 0) {//icon图标
                    UIImageView *icon = [[UIImageView alloc]init];
                    icon.frame = CGRectMake(widthSpace, heightSpace, iconWidth, iconWidth);
                    icon.image = [UIImage imageWithContentsOfFile:[self.delegate getRealPath:icontStr]];
                    icon.backgroundColor = [UIColor clearColor];
                    icon.userInteractionEnabled = NO;
                    [cellBtn addSubview:icon];
                    [icon release];
                }
                //按钮标题
                NSString *titleColor =[btnInfo stringValueForKey:@"titleColor" defaultValue:@"#ffffff"];
                float tilabelX = iconWidth + widthSpace + 5;
                if (iconWidth == 0) {
                    tilabelX = (w - strSize.width)/2.0;
                }
                float lableH = titleSize + 2;
                float labelY = (_bottomRightView.frame.size.height - lableH)/2.0;
                CGRect labelRect = CGRectMake(tilabelX, labelY, strSize.width, lableH);
                UILabel *btnTitleLabel = [[UILabel alloc]initWithFrame:labelRect];
                btnTitleLabel.backgroundColor = [UIColor clearColor];
                btnTitleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
                btnTitleLabel.font = [UIFont systemFontOfSize:titleSize];
                btnTitleLabel.text = titleStr;
                btnTitleLabel.backgroundColor = [UIColor clearColor];
                [cellBtn addSubview:btnTitleLabel];
                [btnTitleLabel release];
                if (icontStr.length > 0) {
                    btnTitleLabel.textAlignment = NSTextAlignmentLeft;
                } else {
                    btnTitleLabel.textAlignment = NSTextAlignmentCenter;
                }
                [_bottomRightView addSubview:cellBtn];
            }
            self.slipDistanceUI = width;
        }
        [self insertSubview:_bottomRightView atIndex:0];
    }
    
    if(!self.bottomLeftView){
        _bottomLeftView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height)];
        float width = 0;
        if (self.leftBtnUI) {
            for (int i=0; i<[leftBtnUI count]; i++){
                NSDictionary *btnInfo = [leftBtnUI objectAtIndex:i];
                float w = [btnInfo floatValueForKey:@"width" defaultValue:self.frame.size.width/4];
                UIButton *btntest = [UIButton buttonWithType:UIButtonTypeCustom];
                btntest.frame = CGRectMake(width, 0, w, _bottomLeftView.frame.size.height);
                width += w;
                [btntest addTarget:self action:@selector(didSelectLeftBtn:) forControlEvents:UIControlEventTouchUpInside];
                NSString *bgColor = [btnInfo stringValueForKey:@"bgColor" defaultValue:@"#388e8e"];
                if (!bgColor.length) {
                    bgColor = @"#388e8e";
                }
                if ([UZAppUtils isValidColor:bgColor]) {
                    btntest.backgroundColor = [UZAppUtils colorFromNSString:bgColor];
                } else {
                    UIImage *img = [UIImage imageWithContentsOfFile:[self.delegate getRealPath:bgColor]];
                    [btntest setBackgroundImage:img forState:UIControlStateNormal];
                }
                NSString *activeBg = [btnInfo stringValueForKey:@"activeBgColor" defaultValue:bgColor];
                if (!activeBg.length) {
                    activeBg = bgColor;
                }
                UIImage *selectColor;
                if ([UZAppUtils isValidColor:activeBg]) {
                     selectColor = [self createImageWithColor:[UZAppUtils colorFromNSString:activeBg]];
                } else {
                    selectColor = [UIImage imageWithContentsOfFile:[self.delegate getRealPath:activeBg]];
                }
                [btntest setBackgroundImage:selectColor forState:UIControlStateHighlighted];
                NSString *titleBtn = [btnInfo objectForKey:@"title"];
                float titleSize = [btnInfo floatValueForKey:@"titleSize" defaultValue:12];
                CGSize constraintSize;
                constraintSize.width = btntest.frame.size.width;
                CGSize strSize = [titleBtn sizeWithFont:[UIFont systemFontOfSize:titleSize]
                                      constrainedToSize:constraintSize
                                          lineBreakMode:NSLineBreakByWordWrapping];
                float iconWidth = [btnInfo floatValueForKey:@"iconWidth" defaultValue:20];
                float heightSpase = (btntest.frame.size.height - iconWidth)/2;
                float widthSpase = (btntest.frame.size.width - iconWidth - strSize.width - 5)/2;
                NSString *icon = [btnInfo stringValueForKey:@"icon" defaultValue:nil];
                if (icon.length > 0) {
                    icon = [self.delegate getRealPath:icon];
                    UIImageView *imgView = [[UIImageView alloc] initWithFrame:CGRectMake(widthSpase,
                                                                                         heightSpase,
                                                                                         iconWidth,
                                                                                         iconWidth)];
                    
                    imgView.image = [UIImage imageWithContentsOfFile:icon];
                    [btntest addSubview:imgView];
                    [imgView release];
                } else {
                    iconWidth = 0;
                }
                UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(widthSpase+iconWidth+5,
                                                                           0,
                                                                           w-widthSpase+iconWidth,
                                                                           _bottomLeftView.frame.size.height)];
                if (iconWidth == 0) {
                    label.textAlignment = NSTextAlignmentCenter;
                } else {
                    label.textAlignment = NSTextAlignmentLeft;
                }
                NSString *titleColor = [btnInfo stringValueForKey:@"titleColor" defaultValue:@"#fff"];
                if (!titleColor.length) {
                    titleColor = @"#fff";
                }
                label.textColor = [UZAppUtils colorFromNSString:titleColor];
                label.text = titleBtn;
                [label setFont:[UIFont systemFontOfSize:titleSize]];
                [btntest addSubview:label];
                [label release];
                btntest.tag = i;
                [_bottomLeftView addSubview:btntest];
            }
        }
        _bottomLeftView.backgroundColor =  [UZAppUtils colorFromNSString:self.leftCellBgColor];
        self.slipLeftDistance = width;
        [self insertSubview:_bottomLeftView atIndex:1];
    }
}

- (void)didSelectLeftBtn:(UIButton *)btn {
    if ([self.delegate respondsToSelector:@selector(cellDidSelectLeftBtn:andTag:)]) {
        [self.delegate cellDidSelectLeftBtn:self andTag:btn.tag];
    }
}

- (void)didSelectRightBtn:(UIButton *)btn {
    if ([self.delegate respondsToSelector:@selector(cellDidSelectRightBtn:andTag:)]) {
        [self.delegate cellDidSelectRightBtn:self andTag:btn.tag];
    }
}

- (void)togglePanelWithFlag {
    switch (_currentStatusUI) {
        case kFeedStatusLeftExpanding:{
            _bottomRightView.alpha=0.0f;
            _bottomLeftView.alpha=1.0f;
        }
            break;
        case kFeedStatusRightExpanding:{
            _bottomRightView.alpha=1.0f;
            _bottomLeftView.alpha=0.0f;
        }
            break;
        case kFeedStatusNormal:{
            [_bottomRightView removeFromSuperview];
            self.bottomRightView=nil;
            [_bottomLeftView removeFromSuperview];
            self.bottomLeftView=nil;
        }
        default:
            break;
    }
}

- (void)tapHandle:(UIButton *)gesture {
    if (self.forbiddenClick) {
        return;
    }
    if ([self.delegate respondsToSelector:@selector(cellDidSelectAvatar:)]) {
        [self.delegate cellDidSelectAvatar:self];
    }
}

- (void)tapHandleArrowBtn:(UIButton *)btn {
    if (self.forbiddenClick) {
        return;
    }
    if ([self.delegate respondsToSelector:@selector(cellDidSelectArrow:)]) {
        [self.delegate cellDidSelectArrow:self];
    }
}

- (void)panGestureHandle:(UIPanGestureRecognizer *)recognizer{
    if (self.forbiddenClick) {
        return;
    }
    if (recognizer.state == UIGestureRecognizerStateBegan){
        _initialTouchPositionX = [recognizer locationInView:self].x;
        _initialHorizontalCenter = self.contentView.center.x;
        if(_currentStatusUI == kFeedStatusNormal){
            [self layoutBottomView];
        }
        if ([self.delegate respondsToSelector:@selector(cellDidBeginPan:)]){
            [self.delegate cellDidBeginPan:self];
        }
    } else if (recognizer.state == UIGestureRecognizerStateChanged){ //status
        CGFloat panAmount  = _initialTouchPositionX - [recognizer locationInView:self].x;
        CGFloat newCenterPosition     = _initialHorizontalCenter - panAmount;
        CGFloat centerX               = self.contentView.center.x;
        if (self.rightBtn && !self.leftBtnUI && panAmount<-20 ) {
            return;
        } else if (self.leftBtnUI && !self.rightBtn && panAmount>20){
            return;
        }
        if (centerX>_originalCenter && _currentStatusUI!=kFeedStatusLeftExpanding) {
            _currentStatusUI = kFeedStatusLeftExpanding;
            [self togglePanelWithFlag];
        } else if (centerX<_originalCenter && _currentStatusUI!=kFeedStatusRightExpanding) {
            _currentStatusUI = kFeedStatusRightExpanding;
            [self togglePanelWithFlag];
        }
        if (panAmount > 0) {
            _lastDirection = LMFeedCellDirectionLeft;
        } else {
            _lastDirection = LMFeedCellDirectionRight;
        }
        if (newCenterPosition > self.frame.size.width + _originalCenter) {
            newCenterPosition = self.frame.size.width + _originalCenter;
        }else if (newCenterPosition < -_originalCenter) {
            newCenterPosition = - _originalCenter;
        }
        CGPoint center = self.contentView.center;
        center.x = newCenterPosition;
        self.contentView.layer.position = center;
    } else if (recognizer.state == UIGestureRecognizerStateEnded ||
            recognizer.state == UIGestureRecognizerStateCancelled){
        CGPoint translation = [recognizer translationInView:self];
        CGFloat velocityX = [recognizer velocityInView:self].x;
        //判断是否push view
        BOOL isNeedPush = (fabs(velocityX) > kMinimumVelocity);
        isNeedPush |= ((_lastDirection == LMFeedCellDirectionLeft && translation.x < -kMinimumPan) ||
                       (_lastDirection== LMFeedCellDirectionRight && translation.x > kMinimumPan));
        if (velocityX > 0 && _lastDirection == LMFeedCellDirectionLeft){
            isNeedPush = NO;
        } else if (velocityX < 0 && _lastDirection == LMFeedCellDirectionRight){
            isNeedPush = NO;
        }
        if (isNeedPush && !self.revealingUI) {
            if(_lastDirection==LMFeedCellDirectionRight){
                _currentStatusUI = kFeedStatusLeftExpanding;
                [self togglePanelWithFlag];
            } else {
                _currentStatusUI = kFeedStatusRightExpanding;
                [self togglePanelWithFlag];
            }
            [self _slideOutContentViewInDirection:_lastDirection];
            [self _setRevealingUI:YES];

        }else if (self.revealingUI && translation.x != 0) {
            LMFeedCellDirection direct = _currentStatusUI==kFeedStatusRightExpanding?LMFeedCellDirectionLeft:LMFeedCellDirectionRight;
            [self _slideInContentViewFromDirection:direct];
            [self _setRevealingUI:NO];
        } else if (translation.x != 0) {
            LMFeedCellDirection finalDir = LMFeedCellDirectionRight;
            if (translation.x < 0)
                finalDir = LMFeedCellDirectionLeft;
            [self _slideInContentViewFromDirection:finalDir];
            [self _setRevealingUI:NO];
        }
    }
}

#pragma mark -
#pragma mark revealing setter
#pragma mark -

- (void)setRevealingUI:(BOOL)revealing{
	if (_revealingUI == revealing) {
		return;
    }
	[self _setRevealingUI:revealing];
	if (self.revealingUI) {
		[self _slideOutContentViewInDirection:_lastDirection];
	} else {
		[self _slideInContentViewFromDirection:_lastDirection];
    }
}

- (void)_setRevealingUI:(BOOL)revealing{
	_revealingUI=revealing;
	if (self.revealingUI && [self.delegate respondsToSelector:@selector(cellDidReveal:)])
		[self.delegate cellDidReveal:self];
}

#pragma mark
#pragma mark - ContentView Sliding 弹动&滑出
#pragma mark

- (void)_slideInContentViewFromDirection:(LMFeedCellDirection)direction {//弹动
	CGFloat bounceDistance;
	switch (direction) {
		case LMFeedCellDirectionRight:
			bounceDistance = kBOUNCE_DISTANCE;
			break;
		case LMFeedCellDirectionLeft:
			bounceDistance = -kBOUNCE_DISTANCE;
			break;
        default:
            bounceDistance = kBOUNCE_DISTANCE;
			break;
	}
    [UIView animateWithDuration:0.1 animations:^{
        self.contentView.center = CGPointMake(_originalCenter, self.contentView.center.y);
        self.contentView.frame = CGRectOffset(self.contentView.frame, bounceDistance, 0);
        self.contentView.frame = CGRectOffset(self.contentView.frame, -bounceDistance, 0);
        _currentStatusUI=kFeedStatusNormal;
        [self togglePanelWithFlag];
    }];
}

- (void)_slideOutContentViewInDirection:(LMFeedCellDirection)direction {//滑出
	CGFloat newCenterX;
    CGFloat bounceDistance;
    switch (direction) {
        case LMFeedCellDirectionLeft:{
            bounceDistance = -kBOUNCE_DISTANCE;
            _currentStatusUI=kFeedStatusLeftExpanded;
            newCenterX = self.contentView.frame.size.width/2.0 - self.slipDistanceUI;
            if (self.leftBtnUI && !self.rightBtn){
                newCenterX = self.contentView.frame.size.width/2.0;
            }
        }
            break;
        case LMFeedCellDirectionRight:{
            newCenterX = self.contentView.frame.size.width/2.0 + self.slipLeftDistance;
            if (self.rightBtn && !self.leftBtnUI) {
                newCenterX = self.contentView.frame.size.width/2 ;
            }
            bounceDistance = kBOUNCE_DISTANCE;
            _currentStatusUI=kFeedStatusRightExpanded;
        }
            break;
        default:
            return;
            break;
    }
	[UIView animateWithDuration:0.1
						  delay:0
						options:UIViewAnimationOptionCurveEaseOut
					 animations:^{
                         self.contentView.center = CGPointMake(newCenterX, self.contentView.center.y);
                     }
                     completion:^(BOOL f) {
						 [UIView animateWithDuration:0.1 delay:0
											 options:UIViewAnimationOptionCurveEaseIn
										  animations:^{
                                              self.contentView.frame = CGRectOffset(self.contentView.frame, -bounceDistance, 0);
                                          }
										  completion:^(BOOL f) {
											  [UIView animateWithDuration:0.1 delay:0
                                                                  options:UIViewAnimationOptionCurveEaseIn
                                                               animations:^{
                                                                   self.contentView.frame = CGRectOffset(self.contentView.frame, bounceDistance, 0);
                                                               }
                                                               completion:NULL];
										  }];
     }];
}

#pragma mark-
#pragma mark  UIGestureRecognizerDelegate
#pragma mark-

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer {
    if (!self.leftBtnUI && !self.rightBtn) {
        return NO;
    }
	if (gestureRecognizer == _panGesture) {
		UIScrollView *superview = (UIScrollView *)self.superview;
		CGPoint translation = [(UIPanGestureRecognizer *)gestureRecognizer translationInView:superview];
		// Make it scrolling horizontally
		return ((fabs(translation.x) / fabs(translation.y) > 1) ? YES : /* DISABLES CODE */ (NO) && (superview.contentOffset.y == 0.0 && superview.contentOffset.x == 0.0));
	}
	return YES;
}

#pragma mark-
#pragma mark utile
#pragma mark-

- (UIImage *)createImageWithColor:(UIColor*)color{//UIColor 转UIImage
    CGRect rect=CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return theImage;
}

@end
