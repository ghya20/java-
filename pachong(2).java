package tusu;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;

public class pachong 
{
    public List<String> pachong1() 
    {       
            List<String> bookList = new ArrayList<>();
            for(int page = 0;page <= 225; page += 25)    
            {
                try 
                {   
                    String urlStr = String.format("https://book.douban.com/top250?start=%d", page);
                    URL url = URI.create(urlStr).toURL();
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 设置请求头，模拟浏览器访问
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0");
                
                    try 
                        (
                        //低级流,字节流
                        InputStream is = connection.getInputStream();
                        //字符流
                        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                        //包装成字符缓冲输入流
                        BufferedReader br = new BufferedReader(isr);
                        ) 
                    { 
                        String line;
                        StringBuilder content = new StringBuilder();
                        while ((line = br.readLine()) != null) 
                        {
                            content.append(line).append("\n");
                        }
                        String htmlContent = content.toString(); 
                        // 正则表达式匹配书名
                        String[] patterns = 
                            {
                                //匹配<a>标签内的title文本，支持括号和常见标点
                                "<a[^>]*title=\"([^\"]*)\"[^>]*>"
                            };
                        //排除这些非书名的词汇
                        Set<String> blacklist = new HashSet<>
                            ( Arrays.asList(
                                "音乐", "播客", "同城", "小组", "阅读", "时间", "豆品", "豆瓣读书", 
                                "购书单", "电子图书", "文学", "小说", "历史文化", "社会纪实", "科学新知",
                                "艺术设计", "商业经管", "绘本漫画", "全部", "关于豆瓣", "在豆瓣工作","可试读",
                                "联系我们", "法律声明", "帮助中心", "图书馆合作", "移动应用", "豆瓣","全新发布",
                                "读书", "电影", "下载豆瓣客户端", "首页", "登录", "注册", "搜索","全新"
                                ,"2024年度榜单","2024年度报告","iPhone","Android","FM","10"
                            ));
                        // 用于去重
                        Set<String> uniqueTitles = new HashSet<>();
                        
                        for (String patternStr : patterns)
                        {
                            Pattern pattern = Pattern.compile(patternStr);
                            Matcher matcher = pattern.matcher(htmlContent);
                            while (matcher.find())
                            {
                            String bookTitle = matcher.group(1).trim();
                            if
                            (   
                                !blacklist.contains(bookTitle) && 
                                bookTitle.length() >= 1 && 
                                bookTitle.length() <= 40 &&
                                uniqueTitles.add(bookTitle)
                            )
                            bookList.add(bookTitle);
                            } 
                        }     
                    }
                }     
                catch (Exception e) 
                {
                    System.err.println("发生错误: " + e.getMessage());
                     e.printStackTrace();
                }    
            }    
        return bookList;    
    }
}