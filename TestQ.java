import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;


public class TestQ
{
   public static void main (String[]args)
   {
       try
       {
           DecimalFormat df = new DecimalFormat("0.00");
           
           //   Declare queue
           Queue qEmina = new Queue();
           Queue qTemp = new Queue();
           Queue qWrongQuant = new Queue();
           
           Scanner input = new Scanner(System.in);
           
           //   To read input from file input
           FileReader fr = new FileReader("CustInfo.txt");
           BufferedReader br = new BufferedReader(fr);
           String read = br.readLine();
           
           System.out.println("\n*************************************  EMINA TIKTOKSHOP ORDERING SYSTEM    *************************************");
           
           //   Loop to store all the data from file input into  queue
           while (read != null)
           {
               StringTokenizer st = new StringTokenizer(read, ";");
               
               String customerName = st.nextToken();
               String customerAddress = st.nextToken();
               String customerPhoneNum = st.nextToken();
               String voucher = st.nextToken();                 //  VC010-Discount 10% | FS045- deduct RM8.00 || NO- doesnt have any voucher
               String paymentMethod = st.nextToken();           //  TRANSFER or COD
               String itemCode = st.nextToken();                //  SKI010/SKI011/SKI012....
               String itemName = st.nextToken();
               String itemDetails = st.nextToken();             //  Combo/Cosmetic/Skincare
               int itemQuantity = Integer.parseInt(st.nextToken());
               
               Customer data = new Customer(customerName,customerAddress,customerPhoneNum,voucher,paymentMethod,itemCode,itemName,itemDetails, itemQuantity);
               
               //   Store in queue
               qEmina.enqueue(data);
               
               read = br.readLine(); 
           }
           
           //   Copy all items that has itemType “Skincare” and put them into qSkincare, 
           //   itemType of “Combo” put in comboLL or otherwise put into qCombo. 
           Queue qSkincare = new Queue();
           Queue qCombo    = new Queue();
           Queue qCosmetic = new Queue();
           while(! qEmina.isEmpty())
           {
               Customer temp = (Customer) qEmina.dequeue();
               qTemp.enqueue(temp);
               
               if (temp.getItemDetails().equalsIgnoreCase("Skincare"))
               {
                   qSkincare.enqueue(temp);
               }
               else if (temp.getItemDetails().equalsIgnoreCase("Combo"))
               {
                   qCombo.enqueue(temp);
               }
               else
               {
                   qCosmetic.enqueue(temp);
               }
           }

           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qEmina.enqueue(qTemp.dequeue());
           }
    
           
           //   Display receipt for customer
           int count = 1;
           while(!qEmina.isEmpty())
           {
               Customer temp =  (Customer) qEmina.dequeue();
               qTemp.enqueue(temp);
               
               double totalItem = temp.calcPrice(temp.getItemCode(),temp.getItemQuantity()) ; 
               double total = totalItem + 8; // Already include shipping RM 8
               double discount = temp.voucherChecker(temp.getVoucher(), total);
               

               String discDescr = "No Discount";
               double delivCharge = 0.00; //   Determine the delivery charge
               if(temp.getVoucher().equalsIgnoreCase("FS045"))
               {
                   delivCharge = 0.00;
                   discDescr = "Free Delivery -RM 8.00";
               }
               else if (temp.getVoucher().equalsIgnoreCase("VC010"))
               {
                   discDescr = "Discount 10%";
               }
               else
               {
                   delivCharge = 8.00;
               }
               
               total = total - discount;
               double tax = total*0.05;
               double totalPrice = total+tax;
               System.out.println("\n\nORDER                    #"+count+"                                     FROM CUSTOMER "+ temp.getCustomerName() + 
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n" + temp.toString() +
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n Total Price For Item                                                             RM " + df.format(totalItem) +
                                   "\n Discount                                                                         RM " + df.format(discount)+ " (" +discDescr+")"+
                                   "\n Delivery Charge                                                                  RM " + df.format(delivCharge) +
                                   "\n Tax                                                                              RM " + df.format(tax) +
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n\n Total Order                                                                      RM " + df.format(totalPrice) + 
                                   "\n----------------------------------------------------------------------------------------------------------------------");
                      
               count++;
           }
           
           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qEmina.enqueue(qTemp.dequeue());
           }
    
           
           
           //   UPDATE quantity of item by search itemCode and 
           boolean found = false;
           String updateConfirmation = JOptionPane.showInputDialog(null,"Do You want to update quantity of item","UPDATE SYSTEM",JOptionPane.QUESTION_MESSAGE);
           while (updateConfirmation.equalsIgnoreCase("Yes"))
           {
               found = false;
               String searchItem = JOptionPane.showInputDialog(null,"CUSTOMER NAME","UPDATE SYSTEM",JOptionPane.QUESTION_MESSAGE);
               int newQuantity = Integer.parseInt(JOptionPane.showInputDialog(null,"UPDATE QUANTITY","UPDATE SYSTEM",JOptionPane.QUESTION_MESSAGE));
               
               while(!qEmina.isEmpty())
               {
                   Customer temp = (Customer) qEmina.dequeue();
                   qTemp.enqueue(temp);
               
                   if (temp.getCustomerName().equalsIgnoreCase(searchItem))
                   {
                       System.out.println("==================================== Customer Order after update ====================================");
                       temp.setItemQuantity(newQuantity);
                       found = true;
                   
                       System.out.println(temp.toString());
                   }
               }    
               
               if (!found)
               {
                   JOptionPane.showMessageDialog(null,"The Name you enter is not in our database.","UPDATE SYSTEM",JOptionPane.WARNING_MESSAGE);
               }
               //   Insert back into queue
               while(!qTemp.isEmpty())
               {
                   qEmina.enqueue(qTemp.dequeue());
               }
               
               updateConfirmation = JOptionPane.showInputDialog(null,"Do You want to update quantity of item","UPDATE SYSTEM",JOptionPane.QUESTION_MESSAGE);
           }
    
           
           //   Remove data of quantity item that wrongly entered and move into special record. 
           while(!qEmina.isEmpty())
           {
               Customer temp = (Customer) qEmina.dequeue();
               
               if (temp.getItemQuantity() <= 0)
               {
                   qWrongQuant.enqueue(temp);
               }
               else
               {
                   qTemp.enqueue(temp);
               }
           }

           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qEmina.enqueue(qTemp.dequeue());
           }
           
           
           /**          REMOVE               */
           
           //   Remove data of quantity item that wrongly entered and move into special record. 
           while(!qEmina.isEmpty())
           {
               Customer temp = (Customer) qEmina.dequeue();
               
               if (temp.getItemQuantity() <= 0)
               {
                   qWrongQuant.enqueue(temp);
               }
               else
               {
                   qTemp.enqueue(temp);
               }
           }

           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qEmina.enqueue(qTemp.dequeue());
           }
           
           
           
           /**          AFTER REMOVE         */
           //   Display receipt for customer
           count = 1;
           System.out.println("\n\n\t*******************************  AFTER REMOVE THE WRONG OUTPUT   *******************************");
           while(!qEmina.isEmpty())
           {
               Customer temp =  (Customer) qEmina.dequeue();
               qTemp.enqueue(temp);
               
               double totalItem = temp.calcPrice(temp.getItemCode(),temp.getItemQuantity()) ; 
               double total = totalItem + 8; // Already include shipping RM 8
               double discount = temp.voucherChecker(temp.getVoucher(), total);
               

               String discDescr = "No Discount";
               double delivCharge = 0.00; //   Determine the delivery charge
               if(temp.getVoucher().equalsIgnoreCase("FS045"))
               {
                   delivCharge = 0.00;
                   discDescr = "Free Delivery -RM 8.00";
               }
               else if (temp.getVoucher().equalsIgnoreCase("VC010"))
               {
                   discDescr = "Discount 10%";
               }
               else
               {
                   delivCharge = 8.00;
               }
               
               total = total - discount;
               double tax = total*0.05;
               double totalPrice = total+tax;
               System.out.println("\n\n\nORDER                    #"+count+"                                     FROM CUSTOMER "+ temp.getCustomerName() + 
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n" + temp.toString() +
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n Total Price For Item                                                             RM " + df.format(totalItem) +
                                   "\n Discount                                                                         RM " + df.format(discount)+ " (" +discDescr+")"+
                                   "\n Delivery Charge                                                                  RM " + df.format(delivCharge) +
                                   "\n Tax                                                                              RM " + df.format(tax) +
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n\n Total Order                                                                      RM " + df.format(totalPrice) + 
                                   "\n----------------------------------------------------------------------------------------------------------------------");
                      
               count++;
           }
           
           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
                qEmina.enqueue(qTemp.dequeue());
           }
               
               
           //   Search and display item based on ItemCode that user key in. 
           found = false;
           String searchConfirmation = JOptionPane.showInputDialog(null,"Do you want to use the search system\n[this search system will list all customer that bought the item code that been search] ","SEARCH SYSTEM",JOptionPane.QUESTION_MESSAGE);
           int countS = 1;
           if (searchConfirmation.equalsIgnoreCase("YES"))
           {
               System.out.println("\n\t------------------------------------------------------------------------------------------------------"
                             +"\n\t\t\t\t\t\tSEARCH SYSTEM" + 
                             "\n\t------------------------------------------------------------------------------------------------------");
           }
           while(searchConfirmation.equalsIgnoreCase("YES"))
           {
               String searchItem = JOptionPane.showInputDialog(null,"Enter the Item Code  ","SEARCH SYSTEM",JOptionPane.QUESTION_MESSAGE);;
               
               //JOptionPane.showInputDialog(null,"Enter the Item Code","SEARCH SYSTEM",JOptionPane.QUESTION_MESSAGE);
               while(!qEmina.isEmpty())
               {
                   Customer temp = (Customer) qEmina.dequeue();
                   qTemp.enqueue(temp);
               
                   if (temp.getItemCode().equalsIgnoreCase(searchItem))
                   {
                       System.out.println("\n NO. : " + countS + temp.toString());
                       found = true;
                       countS++;
                   }
               }
               
               if(!found)
               {
                   JOptionPane.showMessageDialog(null,"The Item Code does not exist in our database.","SEARCH SYSTEM",JOptionPane.WARNING_MESSAGE);
               }

               //   Insert back into queue
               while(!qTemp.isEmpty())
               {
                   qEmina.enqueue(qTemp.dequeue());
               }
               
               searchConfirmation = JOptionPane.showInputDialog(null,"Do you want to use the search system ","SEARCH SYSTEM",JOptionPane.QUESTION_MESSAGE);
           }
           
           
           /**      DISPLAY BEST SELLING ITEM       */
           int bestSelling  = -1;
           String highestCode = null;
           //   Display best selling and least selling item.
           while(!qEmina.isEmpty())
           {
               Customer temp =  (Customer) qEmina.dequeue();
               qTemp.enqueue(temp);
               
               if (temp.getItemQuantity() > bestSelling)
               {
                   bestSelling = temp.getItemQuantity();
                   highestCode = temp.getItemCode();
               }
               
           }
           
           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qEmina.enqueue(qTemp.dequeue());
           }
           
           System.out.println("\n\t\t\t\t-----------------------------------------------" +
                               "\n\t\t\t\t\t\tBEST SELLING ITEM " + 
                               "\n\t\t\t\t------------------------------------------------");
           while(!qEmina.isEmpty())
           {
               Customer temp =  (Customer) qEmina.dequeue();
               qTemp.enqueue(temp);
               
               if ((temp.getItemCode().equalsIgnoreCase(highestCode)) && (temp.getItemQuantity() == bestSelling))
               {
                  System.out.println(
                                     "\n\t\t\t\tItem Code         : " + temp.getItemCode() + 
                                     "\n\t\t\t\tItem Name         : " + temp.getItemName() +
                                     "\n\t\t\t\tItem Details      : " + temp.getItemDetails() +
                                     "\n\t\t\t\tQuantity          : " + temp.getItemQuantity()+
                                     "\n\t\t\t\t------------------------------------------------"+
                                     "\n\t\t\t\t\tORDER BY      : "+ temp.getCustomerName()); 
               }
           }
           
           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qEmina.enqueue(qTemp.dequeue());
           }
           
           /**   FILE OUTPUT    */
           //   Wrongly keyIn quantity of item
           FileWriter fw = new FileWriter("InvalidOrder.txt");
           PrintWriter pw = new PrintWriter(fw);
           count = 1;
           while(!qWrongQuant.isEmpty())
           {
               Customer temp =  (Customer) qWrongQuant.dequeue();
               qTemp.enqueue(temp);
               
               double totalItem = temp.calcPrice(temp.getItemCode(),temp.getItemQuantity()) ; 
               double total = totalItem + 8; // Already include shipping RM 8
               double discount = temp.voucherChecker(temp.getVoucher(), total);
               

               String discDescr = "No Discount";
               double delivCharge = 0.00; //   Determine the delivery charge
               if(temp.getVoucher().equalsIgnoreCase("FS045"))
               {
                   delivCharge = 0.00;
                   discDescr = "Free Delivery -RM 8.00";
               }
               else if (temp.getVoucher().equalsIgnoreCase("VC010"))
               {
                   discDescr = "Discount 10%";
               }
               else
               {
                   delivCharge = 8.00;
               }
               
               total = total - discount;
               double tax = total*0.05;
               double totalPrice = total+tax;
               pw.println("\n\nORDER                    #"+count+"                                     FROM CUSTOMER "+ temp.getCustomerName() + 
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n" + temp.toString() +
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n Total Price For Item                                                             RM " + df.format(totalItem) +
                                   "\n Discount                                                                         RM " + df.format(discount)+ " (" +discDescr+")"+
                                   "\n Delivery Charge                                                                  RM " + df.format(delivCharge) +
                                   "\n Tax                                                                              RM " + df.format(tax) +
                                   "\n----------------------------------------------------------------------------------------------------------------------"+
                                   "\n\n Total Order                                                                      RM " + df.format(totalPrice) + 
                                   "\n----------------------------------------------------------------------------------------------------------------------");
                      
               count++;
           }
           
           //   Insert back into queue
           while(!qTemp.isEmpty())
           {
               qWrongQuant.enqueue(qTemp.dequeue());
           }
           
           pw.close();
           br.close();
       }
       catch(FileNotFoundException fnfe)
        {
            System.out.println(fnfe.getMessage());
        }
        catch(IOException io )
        {
            System.out.println(io.getMessage());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
   }
}
