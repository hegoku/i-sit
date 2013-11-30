//
//  AppDelegate.m
//  isit
//
//  Created by Jack on 13-9-28.
//  Copyright (c) 2013年 Jack. All rights reserved.
//

#import "AppDelegate.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    NSArray *views=[[NSBundle mainBundle] loadNibNamed:@"MainView" owner:self options:nil];
    [self.window addSubview:[views lastObject]];
    
    NSString *urlAsString=@"https://securelogin.arubanetworks.com/auth/index.html/u";
    urlAsString =[urlAsString stringByAppendingString:@"?user=0780"];
    urlAsString =[urlAsString stringByAppendingString:@"&password=600603"];
    
    NSURL *url=[NSURL URLWithString:urlAsString];
    
    NSMutableURLRequest *urlRequest=[NSMutableURLRequest requestWithURL:url];
    [urlRequest setTimeoutInterval:5000.0f];
    [urlRequest setHTTPMethod:@"POST"];
    
    NSString *body=@"user=0780&password=600603";
    
    [urlRequest setHTTPBody:[body dataUsingEncoding:NSUTF8StringEncoding]];
    
    NSOperationQueue *queue=[[NSOperationQueue alloc]init];
    [ NSURLConnection sendAsynchronousRequest:urlRequest queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
        if ([data length]>0&&error==nil)
        {
            NSString *html=[[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
            UIAlertView* alert=[[UIAlertView alloc]initWithTitle:@"提示" message:@"登录成功!" delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil,nil];
			[alert show];
			NSLog(@"HTML=%@",html);
        }
        else if([data length]==0&&error==nil)
        {
            NSLog(@"download nothing");
        }
        else if(error!=nil)
        {
			UIAlertView* alert=[[UIAlertView alloc]initWithTitle:@"提示" message:@"网络错误!" delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil,nil];
			[alert show];
			NSLog(error.description);
        }
        
        
    }];

    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
