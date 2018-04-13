/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import "UZUIListView.h"
#import "UZAppUtils.h"
#import "NSDictionaryUtils.h"
#import "TPGestureTableViewCell_UI.h"
#import "UZUIRefreshTableHeaderView.h"
#import "UZUIRefreshTableFooterView.h"
#import "UIImageView+WebCache.h"
@interface UZUIListView ()
<UITableViewDelegate,UITableViewDataSource, TPGestureTableViewCellDelegate, EGORefreshTableDelegate> {
    NSInteger cbID, refreshHeadcbid, refreshFootercbid;;
    //itemSlipLeftDistance itemSlipDistance
    float cellHeight,headImgHeight,headImgWidth;
    //loading
    BOOL _reloading;
    //EGOHeader
    UZUIRefreshTableHeaderView *_refreshHeaderView;
    //EGOFoot
    UZUIRefreshTableFooterView *_refreshFooterView;
    float imageCorner;
}

@property (nonatomic, strong) NSString *headerPlaceImg;
@property (nonatomic, strong) NSString *borderColor;
@property (nonatomic, strong) NSString *cellBgColor;
@property (nonatomic, strong) NSString *cellSelectColor;
@property (nonatomic, strong) NSArray *leftBtn;
@property (nonatomic, strong) NSArray *rightBtn;
@property (nonatomic, strong) NSMutableArray *dataSource;
@property (nonatomic, strong) UITableView *mainTableView;
@property (nonatomic, strong) NSString *bgcellLeftColor;
@property (nonatomic, strong) NSString *bgcellRightColor;
@property (nonatomic, strong) UZUIRefreshTableFooterView *refreshFooterView;
@property (nonatomic, strong) UZUIRefreshTableHeaderView *refreshHeaderView;
@property (nonatomic, strong) TPGestureTableViewCell_UI *currentCell;
@property (nonatomic, strong) NSDictionary *markStyle;
@property (nonatomic, strong) NSDictionary *styles;
@property (nonatomic, strong) NSDictionary *item;

@end

@implementation UZUIListView

@synthesize currentCell, borderColor, cellBgColor;
@synthesize leftBtn, rightBtn, cellSelectColor;
@synthesize mainTableView;
@synthesize headerPlaceImg;
@synthesize bgcellLeftColor, bgcellRightColor;
@synthesize refreshFooterView = _refreshFooterView, refreshHeaderView = _refreshHeaderView;
@synthesize markStyle;

#pragma mark lifeCycle

- (void)dispose{
    [self close:nil];
}

#pragma mark-
#pragma mark interface
#pragma mark-

- (void)open:(NSDictionary *)paramsDict_ {
    //若非第一次打开则return
    if (self.mainTableView) {
        [[self.mainTableView superview] bringSubviewToFront:self.mainTableView];
        self.mainTableView.hidden = NO;
        return;
    }
    //读取配置参数
    cbID = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    NSDictionary *rect = [[NSDictionary alloc] initWithDictionary:[paramsDict_ dictValueForKey:@"rect" defaultValue:@{}]];
    _styles = [[NSDictionary alloc] initWithDictionary:[paramsDict_ dictValueForKey:@"styles" defaultValue:@{}]];
    float viewx = [rect floatValueForKey:@"x" defaultValue:0];
    float viewy = [rect floatValueForKey:@"y" defaultValue:0];
    NSString * viewName = [paramsDict_ stringValueForKey:@"fixedOn" defaultValue:nil];
    UIView *superView = [self getViewByName:viewName];
    float viewwidth = [rect floatValueForKey:@"w" defaultValue:superView.frame.size.width];
    float viewheight = [rect floatValueForKey:@"h" defaultValue:superView.frame.size.height];
    self.borderColor = [_styles stringValueForKey:@"borderColor" defaultValue:@"#696969"];
    if (!self.borderColor.length) {
        self.borderColor = @"#696969";
    }
    _item = [[NSDictionary alloc] initWithDictionary:[_styles dictValueForKey:@"item" defaultValue:@{}]];
    imageCorner = [_item floatValueForKey:@"imgCorner" defaultValue:0];
    //cell背景色
    self.cellBgColor = [_item stringValueForKey:@"bgColor" defaultValue:@"#AFEEEE"];
    if (!self.cellBgColor.length) {
        self.cellBgColor = @"#AFEEEE";
    }
    self.cellSelectColor = [_item stringValueForKey:@"activeBgColor" defaultValue:@"#F5F5F5"];
    if (!self.cellSelectColor.length) {
        self.cellSelectColor = @"#F5F5F5";
    }
    cellHeight = [_item floatValueForKey:@"height" defaultValue:55.0];
    headImgHeight = [_item floatValueForKey:@"imgHeight" defaultValue:cellHeight-10];
    headImgWidth = [_item floatValueForKey:@"imgWidth" defaultValue:cellHeight-10];
    
    //占位图
    NSString *placehoderImgHeader = [_item stringValueForKey:@"placeholderImg" defaultValue:nil];
    if ([placehoderImgHeader isKindOfClass:[NSString class]] && placehoderImgHeader.length>0) {
        NSString *realPcImgPath = [self getPathWithUZSchemeURL:placehoderImgHeader];
        self.headerPlaceImg = realPcImgPath;
    } else {
        self.headerPlaceImg = nil;
    }
    self.bgcellLeftColor = [_styles stringValueForKey:@"leftBgColor" defaultValue:@"#5cacee"];
    if (!self.bgcellLeftColor.length) {
        self.bgcellLeftColor = @"#5cacee";
    }
    self.bgcellLeftColor = self.cellBgColor;
    self.bgcellRightColor = [_styles stringValueForKey:@"rightBgColor" defaultValue:@"#6c7b8b"];
    if (!self.bgcellRightColor.length) {
        self.bgcellRightColor = @"#6c7b8b";
    }
    self.bgcellRightColor = self.cellBgColor;
    NSArray *tempData = [paramsDict_ arrayValueForKey:@"data" defaultValue:@[]];
    if (!tempData||[tempData count]==0) {
        return;
    }
    self.dataSource = [NSMutableArray arrayWithArray:tempData];
    self.markStyle = [paramsDict_ dictValueForKey:@"markStyle" defaultValue:nil];
    self.leftBtn = [paramsDict_ arrayValueForKey:@"leftBtns" defaultValue:nil];
    self.rightBtn = [paramsDict_ arrayValueForKey:@"rightBtns" defaultValue:nil];
    
    BOOL bounces = [paramsDict_ boolValueForKey:@"bounces" defaultValue:NO];
    BOOL showScrollBar = [paramsDict_ boolValueForKey:@"showScrollBar" defaultValue:YES];

    //初始化列表视图
    UITableView *tableViews = [[UITableView alloc]init];
    tableViews.frame = CGRectMake(viewx, viewy, viewwidth, viewheight);
    tableViews.delegate = self;
    tableViews.dataSource = self;
    tableViews.tag = 987;
    tableViews.bounces = bounces;
    tableViews.backgroundColor = [UIColor clearColor];
    tableViews.separatorStyle = UITableViewCellSeparatorStyleNone;
    tableViews.showsVerticalScrollIndicator = showScrollBar;
    self.mainTableView = tableViews;
    
    //添加到指定窗口
    BOOL fixed = [paramsDict_ boolValueForKey:@"fixed" defaultValue:YES];
    [self addSubview:self.mainTableView fixedOn:viewName fixed:fixed];
    [self view:self.mainTableView preventSlidBackGesture:YES];
    
    //callback
    NSMutableDictionary *cbsenddict = [NSMutableDictionary dictionaryWithCapacity:1];
    [cbsenddict setObject:@"show" forKey:@"eventType"];
    [self sendResultEventWithCallbackId:cbID dataDict:cbsenddict errDict:nil doDelete:NO];
}

- (void)close:(NSDictionary *)paramsDict_ {
    self.headerPlaceImg = nil;
    self.mainTableView.delegate = nil;
    self.mainTableView.dataSource = nil;
    self.currentCell = nil;
    self.borderColor = nil;
    self.cellBgColor = nil;
    self.cellSelectColor = nil;
    self.bgcellRightColor = nil;
    self.bgcellLeftColor = nil;
    if (_refreshHeaderView) {
        [_refreshHeaderView removeFromSuperview];
        self.refreshHeaderView=nil;
    }
    if (_refreshFooterView) {
        [_refreshFooterView removeFromSuperview];
        self.refreshFooterView = nil;
    }
    if (mainTableView) {
        [mainTableView removeFromSuperview];
        self.mainTableView = nil;
    }
}

- (void)reloadData:(NSDictionary *)paramsDict_ {
    if (_refreshHeaderView) {
        _reloading = NO;
        [_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:self.mainTableView];
    }
    NSArray *datas = [paramsDict_ arrayValueForKey:@"data" defaultValue:nil];
    if (datas) {
        [self.dataSource removeAllObjects];
        [self.dataSource addObjectsFromArray:datas];
        [self.mainTableView reloadData];
        [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                                   dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES],@"status", nil]
                                    errDict:nil
                                   doDelete:YES];
    } else {
        [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                                   dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:NO],@"status", nil]
                                    errDict:nil
                                   doDelete:YES];
    }
    if (_refreshFooterView) {
        [self setFooterView:nil];
    }
}

- (void)stopRefresh:(NSDictionary *)paramsDict {
    if (_refreshFooterView) {
        _reloading = NO;
        [_refreshFooterView egoRefreshScrollViewDataSourceDidFinishedLoading:self.mainTableView];
        [self setFooterView:nil];
    }
    NSInteger appendCbid = [paramsDict integerValueForKey:@"cbId" defaultValue:-1];
    [self sendResultEventWithCallbackId:appendCbid
                               dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES],@"status", nil]
                                errDict:nil
                               doDelete:YES];
}

- (void)appendData:(NSDictionary *)paramsDict_{
    NSArray *datas = [paramsDict_ arrayValueForKey:@"data" defaultValue:nil];
    if (datas.count > 0) {
        [self.dataSource addObjectsFromArray:datas];
        [self.mainTableView reloadData];
        [self performSelector:@selector(stopRefresh:) withObject:paramsDict_ afterDelay:0.3];
        return;
    }
    [self stopRefresh:paramsDict_];
}

- (void)setRefreshHeader:(NSDictionary *)paramsDict_{
    refreshHeadcbid = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    self.mainTableView.bounces = YES;
    [self createHeaderView:paramsDict_];
}

- (void)setRefreshFooter:(NSDictionary *)paramsDict_{
    refreshFootercbid = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    self.mainTableView.bounces = YES;
    [self setFooterView:paramsDict_];
}

- (void)hide:(NSDictionary *)paramsDict_{
    if (self.mainTableView) {
        self.mainTableView.hidden = YES;
    }
}

- (void)show:(NSDictionary *)paramsDict_{
    if (self.mainTableView) {
        self.mainTableView.hidden = NO;
    }
}

- (void)deleteItem:(NSDictionary *)paramsDict_{
    BOOL is = NO;
    if ([paramsDict_ objectForKey:@"index"]) {
        int index = (int)[paramsDict_ integerValueForKey:@"index" defaultValue:0];
        if (index>=0 && index<self.dataSource.count) {
            [self.dataSource removeObjectAtIndex:index];
            [self.mainTableView reloadData];
            if (_refreshFooterView && [_refreshFooterView superview]){
                CGFloat height = MAX(self.mainTableView.contentSize.height-cellHeight, self.mainTableView.frame.size.height);
                // reset position
                _refreshFooterView.frame = CGRectMake(0.0f,
                                                      height,
                                                      self.mainTableView.frame.size.width,
                                                      self.mainTableView.bounds.size.height);
            }
            is = YES;
        }
    } else {
        if (self.dataSource.count > 0) {
            NSInteger index = 0;
            [self.dataSource removeObjectAtIndex:index];
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:index inSection:0];
            [self.mainTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationNone];
            if (_refreshFooterView && [_refreshFooterView superview]){
                CGFloat height = MAX(self.mainTableView.contentSize.height-cellHeight, self.mainTableView.frame.size.height);
                // reset position
                _refreshFooterView.frame = CGRectMake(0.0f,
                                                      height,
                                                      self.mainTableView.frame.size.width,
                                                      self.mainTableView.bounds.size.height);
            }
            is = YES;
        }
    }
    [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                               dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:is],@"status", nil]
                                errDict:nil
                               doDelete:YES];
    
}

- (void)updateItem:(NSDictionary *)paramsDict_ {
    BOOL is = NO;
    NSInteger index = [paramsDict_ integerValueForKey:@"index" defaultValue:0];
    if (index>=0 && index<self.dataSource.count) {
        NSDictionary *data = [paramsDict_ dictValueForKey:@"data" defaultValue:@{}];
        if (data&&[data isKindOfClass:[NSDictionary class]] && data.count > 0) {
            self.dataSource[index] = data;
            NSIndexPath *indexPath=[NSIndexPath indexPathForRow:index inSection:0];
            [self.mainTableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath,nil] withRowAnimation:UITableViewRowAnimationNone];
            is = YES;
        }
    }
    [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                               dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:is],@"status", nil]
                                errDict:nil
                               doDelete:YES];
}

- (void)insertItem:(NSDictionary *)paramsDict_ {
    BOOL is = NO;
    NSInteger index = [paramsDict_ integerValueForKey:@"index" defaultValue:self.dataSource.count];
    if (index >= 0 && index <= self.dataSource.count) {
        NSDictionary *data = [paramsDict_ dictValueForKey:@"data" defaultValue:@{}];
        if (data && data.count>0) {
            [self.dataSource insertObject:data atIndex:index];
            [self.mainTableView reloadData];
            if (_refreshFooterView && [_refreshFooterView superview]){
                CGFloat height = MAX(self.mainTableView.contentSize.height+cellHeight, self.mainTableView.frame.size.height);
                // reset position
                _refreshFooterView.frame = CGRectMake(0.0f,
                                                      height,
                                                      self.mainTableView.frame.size.width,
                                                      self.mainTableView.bounds.size.height);
            }
            [self.mainTableView reloadData];
            is = YES;
        }
    }
    [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                               dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:is],@"status", nil]
                                errDict:nil
                               doDelete:YES];
}

- (void)setAttr:(NSDictionary *)paramsDict_ {
    BOOL anim = [paramsDict_ boolValueForKey:@"animation" defaultValue:NO];
    float y = [paramsDict_ floatValueForKey:@"y" defaultValue:self.mainTableView.frame.origin.y];
    float h = [paramsDict_ floatValueForKey:@"h" defaultValue:self.mainTableView.frame.size.height];
    CGRect rect = self.mainTableView.frame;
    rect.origin.y = y;
    rect.size.height = h;
    if (anim){
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:0.3];
        self.mainTableView.frame = rect;
        [UIView commitAnimations];
    } else {
        self.mainTableView.frame = rect;
    }
    [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                               dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES],@"status", nil]
                                errDict:nil
                               doDelete:YES];
}

- (void)getData:(NSDictionary *)paramsDict_ {
    NSInteger cbId = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    NSInteger index = [paramsDict_ integerValueForKey:@"index" defaultValue:0];
    if (cbId < 0){
        return;
    }
    if (index < 0 || index>=self.dataSource.count){
        return;
    }
    NSDictionary *data = [self.dataSource objectAtIndex:index];
    NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [sendDict setObject:data forKey:@"data"];
    [self sendResultEventWithCallbackId:cbId dataDict:sendDict errDict:nil doDelete:YES];
}

- (void)getIndex:(NSDictionary *)paramsDict_ {
    NSInteger cbId = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    NSString *key = [paramsDict_ stringValueForKey:@"key" defaultValue:nil];
    NSString *value = [paramsDict_ stringValueForKey:@"value" defaultValue:nil];
    if (cbId<0 || key.length==0 || value.length==0){
        return;
    }
    for (int i=0; i<self.dataSource.count; i++) {
        NSDictionary *data = [self.dataSource objectAtIndex:i];
        NSString *dataValue = [data stringValueForKey:key defaultValue:nil];
        if ([dataValue isEqualToString:value]) {
            NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
            [sendDict setObject:[NSNumber numberWithInt:i] forKey:@"index"];
            [sendDict setObject:data forKey:@"data"];
            [self sendResultEventWithCallbackId:cbId dataDict:sendDict errDict:nil doDelete:YES];
            break;
        }
    }
}

- (void)getCount:(NSDictionary *)paramsDict_ {
    if (self.mainTableView) {
        NSInteger cbId = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
        [self sendResultEventWithCallbackId:cbId dataDict:[NSDictionary dictionaryWithObject:[NSNumber numberWithInteger:self.dataSource.count] forKey:@"count"] errDict:nil doDelete:YES];
    }
}

- (void)getDataByIndex:(NSDictionary *)paramsDict_ {
    NSInteger index = [paramsDict_ integerValueForKey:@"index" defaultValue:0];
    if (index >= 0 && index < self.dataSource.count) {
        NSDictionary *data = self.dataSource[index];
        [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                                   dataDict:[NSDictionary dictionaryWithObjectsAndKeys:data,@"data", nil]
                                    errDict:nil
                                   doDelete:YES];
    }
}

- (void)setSwipeBtns:(NSDictionary *)paramsDict_ {
    NSInteger index = [paramsDict_ integerValueForKey:@"index" defaultValue:0];
    if (index < 0 || index >= self.dataSource.count) {
        [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                                   dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:NO],@"status", nil]
                                    errDict:nil
                                   doDelete:YES];
        return;
    }
    NSString *type = [paramsDict_ stringValueForKey:@"type" defaultValue:@"right"];
    NSArray *btns = [paramsDict_ arrayValueForKey:@"btns" defaultValue:nil];
    if ([type isEqualToString:@"right"]) {
        NSMutableDictionary *data = [self.dataSource objectAtIndex:index];
        if (btns && btns.count>0 ) {
            [data setValue:btns forKey:@"rightBtns"];
        }
        [self.dataSource replaceObjectAtIndex:index withObject:data];
        [self.mainTableView reloadData];
    } else if ([type isEqualToString:@"left"]) {
        NSDictionary *data = [self.dataSource objectAtIndex:index];
        if (btns && btns.count>0 ) {
            [data setValue:btns forKey:@"leftBtns"];
        }
        [self.dataSource replaceObjectAtIndex:index withObject:data];
        [self.mainTableView reloadData];
    }
    [self sendResultEventWithCallbackId:[paramsDict_ integerValueForKey:@"cbId" defaultValue:-1]
                               dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES],@"status", nil]
                                errDict:nil
                               doDelete:YES];
}


#pragma mark-
#pragma mark refreshView&reloadView
#pragma mark -

- (void)createHeaderView:(NSDictionary *)info{//添加下拉刷新
    if (_refreshHeaderView && [_refreshHeaderView superview]) {
        [_refreshHeaderView removeFromSuperview];
    }
    NSString *bgColor = [info stringValueForKey:@"bgColor" defaultValue:@"#F5F5F5"];
    NSString *loadingImg = [info stringValueForKey:@"loadingImg" defaultValue:nil];
    NSString *realLoadingImg = [self getPathWithUZSchemeURL:loadingImg];
    NSString *textColor = [info stringValueForKey:@"textColor" defaultValue:@"#8E8E8E"];
    NSString *textDown = [info stringValueForKey:@"textDown" defaultValue:@"下拉可以刷新..."];
    NSString *textUp = [info stringValueForKey:@"textUp" defaultValue:@"松开开始刷新..."];
    BOOL showTime = [info boolValueForKey:@"showTime" defaultValue:YES];
    _refreshHeaderView = [[UZUIRefreshTableHeaderView alloc] initWithFrame:
                          CGRectMake(0.0f, 0.0f - self.mainTableView.bounds.size.height, self.mainTableView.frame.size.width, self.mainTableView.bounds.size.height)
                                                            arrowImageName:realLoadingImg
                                                                 textColor:[UZAppUtils colorFromNSString:textColor]
                                                                  withTips:textDown
                                                                  showTime:showTime];
    _refreshHeaderView.delegate = self;
    _refreshHeaderView.textUp = textUp;
    _refreshHeaderView.backgroundColor = [UZAppUtils colorFromNSString:bgColor];
    [mainTableView addSubview:_refreshHeaderView];
    [_refreshHeaderView refreshLastUpdatedDate];
}

- (void)setFooterView:(NSDictionary *)info {//上拉加载更多
    CGFloat height = MAX(self.mainTableView.contentSize.height, self.mainTableView.frame.size.height);
    if (_refreshFooterView && [_refreshFooterView superview]){
        // reset position
        _refreshFooterView.frame = CGRectMake(0.0f,
                                              height,
                                              self.mainTableView.frame.size.width,
                                              self.mainTableView.bounds.size.height);
    } else {
        NSString *bgColor = [info stringValueForKey:@"bgColor" defaultValue:@"#F5F5F5"];
        NSString *loadingImg = [info stringValueForKey:@"loadingImg" defaultValue:nil];
        NSString *realLoadingImg = [self getPathWithUZSchemeURL:loadingImg];
        NSString *textColor = [info stringValueForKey:@"textColor" defaultValue:@"#8E8E8E"];
        NSString *textDown = [info stringValueForKey:@"textDown" defaultValue:@"上拉可加载更多..."];
        NSString *textUp = [info stringValueForKey:@"textUp" defaultValue:@"松开开始加载..."];
        BOOL showTime = [info boolValueForKey:@"showTime" defaultValue:YES];
        // create the footerView
        _refreshFooterView = [[UZUIRefreshTableFooterView alloc] initWithFrame:
                              CGRectMake(0.0f, height,
                                         self.mainTableView.frame.size.width, self.mainTableView.bounds.size.height)arrowImageName:realLoadingImg textColor:[UZAppUtils colorFromNSString:textColor] withTips:textUp showTime:showTime];
        _refreshFooterView.delegate = self;
        _refreshFooterView.textUp = textDown;
        _refreshFooterView.backgroundColor = [UZAppUtils colorFromNSString:bgColor];
        [self.mainTableView addSubview:_refreshFooterView];
    }
    if (_refreshFooterView){
        [_refreshFooterView refreshLastUpdatedDate];
    }
}

#pragma mark -
#pragma mark  refresh&reload's Delegate
#pragma mark -

- (void)egoRefreshTableDidTriggerRefresh:(EGORefreshPos)aRefreshPos{
    _reloading = YES;
    if (aRefreshPos == EGORefreshHeader){
        [self performSelector:@selector(refreshView) withObject:nil];
        //[self performSelector:@selector(refreshView) withObject:nil afterDelay:2.0];
    } else if (aRefreshPos == EGORefreshFooter){
        [self performSelector:@selector(getNextPageView) withObject:nil];
        //[self performSelector:@selector(getNextPageView) withObject:nil afterDelay:2.0];
    }
}

- (BOOL)egoRefreshTableDataSourceIsLoading:(UIView*)view{
    return _reloading;
}

- (NSDate *)egoRefreshTableDataSourceLastUpdated:(UIView *)view{
    return [NSDate date];
}

- (void)refreshView{//下拉刷新事件回调
    [self sendResultEventWithCallbackId:refreshHeadcbid dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES],@"status", nil] errDict:nil doDelete:NO];
}

- (void)getNextPageView{//上拉加载更多事件回调
    [self sendResultEventWithCallbackId:refreshFootercbid dataDict:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES],@"status", nil] errDict:nil doDelete:NO];
}

- (void)finishReloadingData {
    _reloading = NO;
    if (_refreshHeaderView) {
        [_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:self.mainTableView];
    }
    if (_refreshFooterView) {
        [_refreshFooterView egoRefreshScrollViewDataSourceDidFinishedLoading:self.mainTableView];
        [self setFooterView:nil];
    }
}

#pragma mark-
#pragma mark tableViewDelegate
#pragma mark-
#pragma mark dataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self.dataSource count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *cellIdentifier = @"LomemoBasicCell";
    TPGestureTableViewCell_UI *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil) {
        CGSize point = CGSizeMake(headImgWidth, headImgHeight);
        cell = [[TPGestureTableViewCell_UI alloc] initWithStyle:UITableViewCellStyleDefault
                                             reuseIdentifier:cellIdentifier
                                              andBorderColor:self.borderColor andHeight:cellHeight
                                                 withImgSize:point withMarkStyle:self.markStyle
                                                 borderWidth:[_styles floatValueForKey:@"borderWidth" defaultValue:1.0]];
        cell.delegate=self;
        cell.rightBtn = self.rightBtn;
        cell.leftBtnUI = self.leftBtn;
        cell.leftCellBgColor = self.bgcellLeftColor;
        cell.rightCellBgColor = self.bgcellRightColor;
        //cell的背景设置
        cell.selectionStyle = UITableViewCellSelectionStyleDefault;
        UIView *temp = [[UIView alloc] initWithFrame:self.mainTableView.frame];
        temp.backgroundColor = [UZAppUtils colorFromNSString:cellSelectColor];
        cell.selectedBackgroundView = temp;
        cell.iconImg.layer.cornerRadius = imageCorner;
    }
    //背景设置
    cell.contentView.backgroundColor = [UZAppUtils colorFromNSString:cellBgColor];
    //cell.backgroundColor = [UZAppUtils colorFromNSString:cellBgColor];
    NSDictionary *cellInfo = [self.dataSource objectAtIndex:indexPath.row];
    if (![cellInfo isKindOfClass:[NSDictionary class]]) {
        cellInfo = @{};
    }
    //左右
    NSArray *rightBtnInfo = [[NSArray alloc] initWithArray:[cellInfo arrayValueForKey:@"rightBtns" defaultValue:nil]];
    if ([rightBtnInfo isKindOfClass:[NSArray class]] && rightBtnInfo.count > 0) {
        cell.rightBtn = rightBtnInfo;
    } else {
        cell.rightBtn = self.rightBtn;
    }
    NSArray *leftBtnInfo = [[NSArray alloc] initWithArray:[cellInfo arrayValueForKey:@"leftBtns" defaultValue:nil]];
    if ([leftBtnInfo isKindOfClass:[NSArray class]] && leftBtnInfo.count>0) {
        cell.leftBtnUI = leftBtnInfo;
    } else {
        cell.leftBtnUI = self.leftBtn;
    }
    float widthSpace = cell.iconImg.frame.origin.x;
    NSString *remark = [cellInfo stringValueForKey:@"remark" defaultValue:nil];
    float remarkSize = [_item floatValueForKey:@"remarkSize" defaultValue:16];
    CGSize remarkRect = [remark sizeWithFont:[UIFont systemFontOfSize:remarkSize]];
    float width = [_item floatValueForKey:@"remarkIconWidth" defaultValue:30];
    //备注图标
    NSString *arrowPath = [cellInfo stringValueForKey:@"icon" defaultValue:nil];
    UIImage *image = [UIImage imageWithContentsOfFile:[self getPathWithUZSchemeURL:arrowPath]];
    if (arrowPath.length) {
        cell.arrow.image = image;
        float temp = width - cell.arrow.frame.size.width;
        cell.arrow.frame = CGRectMake(self.mainTableView.frame.size.width - width -widthSpace,
                                      cell.arrow.frame.origin.y-temp/2,
                                      width,
                                      width);
        width += widthSpace;
    } else {
        cell.arrow.image = [UIImage imageNamed:@""];
        width = 0;
    }
    cell.originalCenter = tableView.frame.size.width/2;
    //设置右边备注
    NSString *remarkColor = [_item stringValueForKey:@"remarkColor" defaultValue:@"#000"];
    if (!remarkColor.length) {
        remarkColor = @"#000";
    }
    cell.remark.textColor = [UZAppUtils colorFromNSString:remarkColor];
    cell.remark.text = remark;
    CGRect rect = cell.remark.frame;
    rect.size.width = tableView.frame.size.width/2-width-widthSpace;
    rect.origin.x = tableView.frame.size.width/2;
    cell.remark.frame = rect;
    cell.remark.font = [UIFont systemFontOfSize:remarkSize];
    float arrowWidth = width+widthSpace+remarkRect.width;
    if (arrowWidth > self.mainTableView.frame.size.width/2) {
        arrowWidth = self.mainTableView.frame.size.width/2;
    }
    float arrowX = self.mainTableView.frame.size.width-width-widthSpace-remarkRect.width;
    if (arrowX < self.mainTableView.frame.size.width/2) {
        arrowX = self.mainTableView.frame.size.width/2;
    }
    cell.arrowBtn.frame = CGRectMake(arrowX,0,arrowWidth,cellHeight);
    //头像
    UIImage *placeholdImg = [UIImage imageWithContentsOfFile:self.headerPlaceImg];
    NSString *headImg = [cellInfo stringValueForKey:@"imgPath" defaultValue:@""];
    if ([headImg hasPrefix:@"http"]||[headImg hasPrefix:@"https"]) {
        if (placeholdImg) {
            [cell.iconImg sd_setImageWithURL:[NSURL URLWithString:headImg] placeholderImage:placeholdImg];
        } else {
            NSString *placeholdImgStr = [[NSBundle mainBundle]pathForResource:@"res_UIListView/apicloud" ofType:@"png"];
            [cell.iconImg sd_setImageWithURL:[NSURL URLWithString:headImg] placeholderImage:[UIImage imageWithContentsOfFile:placeholdImgStr]];
            
        }
    }else {
        
        if ([self getPathWithUZSchemeURL:headImg]) {
            [cell.iconImg setImage:[UIImage imageNamed:[self getPathWithUZSchemeURL:headImg]]];

        }else if(placeholdImg){
            [cell.iconImg setImage:placeholdImg];

        }
        
    }
    //判断是否有图片。来决定是否修改title，subtitle的坐标
    cell.iconImg.hidden = NO;

    UIImage *imageHead = [UIImage imageNamed: [self getPathWithUZSchemeURL:headImg]];
    if ( imageHead || [headImg hasPrefix:@"http"]||[headImg hasPrefix:@"https"] ) {
        CGRect titleRect = cell.titleLabel.frame;
        CGRect detailRect =  cell.detailTextViewUI.frame;
        titleRect.origin.x = cell.iconImg.frame.size.width+cell.iconImg.frame.origin.x*2;
        detailRect.origin.x = cell.iconImg.frame.size.width+cell.iconImg.frame.origin.x*2;
        titleRect.size.width = self.mainTableView.frame.size.width - titleRect.origin.x;
        detailRect.size.width = self.mainTableView.frame.size.width - detailRect.origin.x;
        cell.titleLabel.frame = titleRect;
        cell.detailTextViewUI.frame = detailRect;
        cell.iconImg.hidden = NO;
    } else {
        CGRect titleRect = cell.titleLabel.frame;
        CGRect detailRect =  cell.detailTextViewUI.frame;
        titleRect.origin.x = 10;
        detailRect.origin.x = 10;
        titleRect.size.width = self.mainTableView.frame.size.width - 10;
        detailRect.size.width = self.mainTableView.frame.size.width - 10;
        cell.titleLabel.frame = titleRect;
        cell.detailTextViewUI.frame = detailRect;
        cell.iconImg.hidden = YES;
    }
    NSString *title = [cellInfo stringValueForKey:@"title" defaultValue:@""];
    cell.titleLabel.text = title;
    //title是否上下居中
    float subtitlesize = [_item floatValueForKey:@"subTitleSize" defaultValue:12];
    NSString *subtitleStr = [cellInfo stringValueForKey:@"subTitle" defaultValue:@""];
    cell.detailTextViewUI.text = subtitleStr;
    if (subtitleStr.length>0 && [subtitleStr isKindOfClass:[NSString class]]) {
        if (!title.length) {
            CGRect subTitleRect = cell.detailTextViewUI.frame;
            subTitleRect.origin.y = 0;
            subTitleRect.size.height = cellHeight;
            subTitleRect.size.width = self.mainTableView.frame.size.width - subTitleRect.origin.x;
            cell.detailTextViewUI.frame = subTitleRect;
        } else {
            CGRect cellTiltle = cell.titleLabel.frame;
            cellTiltle.origin.y = cell.iconImg.frame.origin.y;
            cellTiltle.size.height = cell.iconImg.frame.size.height/2;
            cellTiltle.size.width = self.mainTableView.frame.size.width - cellTiltle.origin.x;
            [cell.titleLabel setFrame:cellTiltle];
            CGRect subTitleRect = cell.detailTextViewUI.frame;
            subTitleRect.origin.y = cell.titleLabel.frame.size.height+cell.titleLabel.frame.origin.y;
            subTitleRect.size.height = cell.iconImg.frame.size.height/2;
            subTitleRect.size.width = self.mainTableView.frame.size.width - subTitleRect.origin.x;
            cell.detailTextViewUI.frame = subTitleRect;
        }
    } else {
        //若无subtitle，title居中
        CGRect cellTiltle = cell.titleLabel.frame;
        cellTiltle.size.height = cellHeight;
        cellTiltle.origin.y = 0;
        [cell.titleLabel setFrame:cellTiltle];
    }
    cell.detailTextViewUI.font = [UIFont systemFontOfSize:subtitlesize];
    cell.cellIndex = indexPath.row;
    float remarkWidth = width + widthSpace + remarkRect.width;
    if (remarkRect.width != 0) {
        remarkWidth += widthSpace;
    }
    CGRect titleRect = cell.titleLabel.frame;
    titleRect.size.width = tableView.frame.size.width - titleRect.origin.x - remarkWidth;
    cell.titleLabel.frame = titleRect;
    //标题是否左右居中
    NSString *titleLoc = [_item stringValueForKey:@"titleAlign" defaultValue:@"left"];
    if (titleLoc.length>0 && [titleLoc isKindOfClass:[NSString class]]) {
        if ([titleLoc isEqualToString:@"left"]) {
            cell.titleLabel.textAlignment = NSTextAlignmentLeft;
        } else if ([titleLoc isEqualToString:@"center"]){
            cell.titleLabel.textAlignment = NSTextAlignmentCenter;
        } else if ([titleLoc isEqualToString:@"right"]){
            cell.titleLabel.textAlignment = NSTextAlignmentRight;
        } else {
            cell.titleLabel.textAlignment = NSTextAlignmentLeft;
        }
    } else {
        cell.titleLabel.textAlignment = NSTextAlignmentLeft;
    }
    CGRect detaRect = cell.detailTextViewUI.frame;
    detaRect.size.width = tableView.frame.size.width - detaRect.origin.x - remarkWidth;
    cell.detailTextViewUI.frame = detaRect;
    //子标题是否左右居中
    NSString *subtitleLoc = [_item stringValueForKey:@"subTitleAlign" defaultValue:@"left"];
    if (subtitleLoc.length>0 && [subtitleLoc isKindOfClass:[NSString class]]) {
        if ([subtitleLoc isEqualToString:@"left"]) {
            cell.detailTextViewUI.textAlignment = NSTextAlignmentLeft;
        } else if ([subtitleLoc isEqualToString:@"center"]){
            cell.detailTextViewUI.textAlignment = NSTextAlignmentCenter;
        } else if ([subtitleLoc isEqualToString:@"right"]){
            cell.detailTextViewUI.textAlignment = NSTextAlignmentRight;
        }
    }
    //标题字体颜色大小
    NSString *titleColor = [_item stringValueForKey:@"titleColor" defaultValue:@"#000000"];
    float titlsize = [_item floatValueForKey:@"titleSize" defaultValue:12];
    if (titleColor.length>0 && [titleColor isKindOfClass:[NSString class]]) {
        cell.titleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
    } else {
        cell.titleLabel.textColor = [UZAppUtils colorFromNSString:@"#000000"];
    }
    cell.titleLabel.font = [UIFont systemFontOfSize:titlsize];
    //子标题字体颜色大小
    NSString *subtitleColor = [_item stringValueForKey:@"subTitleColor" defaultValue:@"#000000"];
    if (subtitleColor.length>0 && [subtitleColor isKindOfClass:[NSString class]]) {
        cell.detailTextViewUI.textColor = [UZAppUtils colorFromNSString:subtitleColor];
    } else {
        cell.detailTextViewUI.textColor = [UZAppUtils colorFromNSString:@"#000000"];
    }
    //最后一条cell的分割线
    if (indexPath.row == self.dataSource.count-1) {
        cell.botLine.hidden = NO;
    } else {
        cell.botLine.hidden = YES;
    }
    cell.botLine.hidden = NO;
    BOOL forbidden = [cellInfo boolValueForKey:@"forbidden" defaultValue:NO];
    if (forbidden) {
        NSDictionary *forbiddenDict = [_styles dictValueForKey:@"forbidden" defaultValue:@{}];
        NSString *bgColor = [forbiddenDict stringValueForKey:@"bgColor" defaultValue:@"#C0C0C0"];
        NSString *titleColor = [forbiddenDict stringValueForKey:@"titleColor" defaultValue:@"#808080"];
        NSString *subTitleColor = [forbiddenDict stringValueForKey:@"subTitleColor" defaultValue:@"#808080"];
        NSString *remarkColor = [forbiddenDict stringValueForKey:@"remarkColor" defaultValue:@"#808080"];
        cell.contentView.backgroundColor = [UZAppUtils colorFromNSString:bgColor];
        cell.titleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
        cell.detailTextViewUI.textColor = [UZAppUtils colorFromNSString:subTitleColor];
        cell.remark.textColor = [UZAppUtils colorFromNSString:remarkColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    } else {
        cell.selectionStyle = UITableViewCellSelectionStyleDefault;
    }
    cell.forbiddenClick = forbidden;
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSDictionary *cellInfo = [self.dataSource objectAtIndex:indexPath.row];
    BOOL forbidden = [cellInfo boolValueForKey:@"forbidden" defaultValue:NO];
    if (forbidden) {
        return;
    }
    TPGestureTableViewCell_UI *cell = (TPGestureTableViewCell_UI*)[tableView cellForRowAtIndexPath:indexPath];
    if(cell.revealingUI){//侧滑按钮是否已经露出
        cell.revealingUI = NO;
        [tableView deselectRowAtIndexPath:indexPath animated:NO];
        return;
    }
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    //[self performSelector:@selector(deselect:) withObject:cell afterDelay:0.1f];
    self.currentCell.revealingUI = NO;
    NSMutableDictionary *cbsenddict = [NSMutableDictionary dictionary];
    [cbsenddict setObject:[NSNumber numberWithInteger:cell.cellIndex] forKey:@"index"];
    [cbsenddict setObject:@"clickContent" forKey:@"eventType"];
    [self sendResultEventWithCallbackId:cbID dataDict:cbsenddict errDict:nil doDelete:NO];
}

- (void)deselect:(TPGestureTableViewCell_UI *)cell{
    [mainTableView deselectRowAtIndexPath:[mainTableView indexPathForCell:cell] animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return cellHeight;
}

#pragma mark -
#pragma mark TPGestureTableViewCellDelegate
#pragma mark -

- (NSString *)getRealPath:(NSString *)path {
    return  [self getPathWithUZSchemeURL:path];
}

- (void)cellDidReveal:(TPGestureTableViewCell_UI *)cell{
    if(self.currentCell!=cell){
        self.currentCell.revealingUI=NO;
        self.currentCell=cell;
    }
}

- (void)cellDidSelectLeftBtn:(TPGestureTableViewCell_UI *)cell andTag:(NSInteger)tag {
    self.currentCell.revealingUI = NO;
    NSMutableDictionary *cbsenddict = [NSMutableDictionary dictionaryWithCapacity:2];
    [cbsenddict setObject:[NSNumber numberWithInteger:cell.cellIndex] forKey:@"index"];
    [cbsenddict setObject:@"clickLeftBtn" forKey:@"eventType"];
    [cbsenddict setObject:[NSNumber numberWithInteger:tag] forKey:@"btnIndex"];
    [self sendResultEventWithCallbackId:cbID dataDict:cbsenddict errDict:nil doDelete:NO];
}

- (void)cellDidSelectRightBtn:(TPGestureTableViewCell_UI *)cell andTag:(NSInteger)tag{
    self.currentCell.revealingUI = NO;
    NSMutableDictionary *cbsenddict = [NSMutableDictionary dictionaryWithCapacity:2];
    [cbsenddict setObject:[NSNumber numberWithInteger:cell.cellIndex] forKey:@"index"];
    [cbsenddict setObject:@"clickRightBtn" forKey:@"eventType"];
    [cbsenddict setObject:[NSNumber numberWithInteger:tag] forKey:@"btnIndex"];
    [self sendResultEventWithCallbackId:cbID dataDict:cbsenddict errDict:nil doDelete:NO];
}

- (void)cellDidSelectAvatar:(TPGestureTableViewCell_UI *)cell{
    
    self.currentCell.revealingUI = NO;
    NSMutableDictionary *cbsenddict = [NSMutableDictionary dictionaryWithCapacity:2];
    [cbsenddict setObject:[NSNumber numberWithInteger:cell.cellIndex] forKey:@"index"];
    [cbsenddict setObject:@"clickImg" forKey:@"eventType"];
    [self sendResultEventWithCallbackId:cbID dataDict:cbsenddict errDict:nil doDelete:NO];
 
}

- (void)cellDidSelectArrow:(TPGestureTableViewCell_UI *)cell{
    self.currentCell.revealingUI = NO;
    NSMutableDictionary *cbsenddict = [NSMutableDictionary dictionaryWithCapacity:2];
    [cbsenddict setObject:[NSNumber numberWithInteger:cell.cellIndex] forKey:@"index"];
    [cbsenddict setObject:@"clickRemark" forKey:@"eventType"];
    [self sendResultEventWithCallbackId:cbID dataDict:cbsenddict errDict:nil doDelete:NO];
}

#pragma mark -
#pragma mark ScrollowViewDelegate
#pragma mark -

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if (self.currentCell.revealingUI) {
        self.currentCell.revealingUI = NO;
    }
    if (_refreshHeaderView){
        [_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
    }
    if (_refreshFooterView){
        [_refreshFooterView egoRefreshScrollViewDidScroll:scrollView];
        [self setFooterView:nil];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if (_refreshHeaderView){
        [_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
    }
    if (_refreshFooterView){
        [_refreshFooterView egoRefreshScrollViewDidEndDragging:scrollView];
    }
}

@end
