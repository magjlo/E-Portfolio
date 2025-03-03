

.macro	PUSH (%reg)
	addi	$sp,$sp,-4
	sw	%reg,0($sp)
.end_macro

.macro	POP (%reg)
	lw	%reg,0($sp)
	addi	$sp,$sp,4
.end_macro

	.data
	.align 2
mytime:	.word 0x5957
timstr:	.ascii "text more text lots of text\0"
	.text
main:
	# print timstr
	la	$a0,timstr
	li	$v0,4
	syscall
	nop
	# wait a little
	li	$a0,0		# (M) : variable ms
	jal	delay
	nop
	# call tick
	la	$a0,mytime
	jal	tick
	nop
	# call your function time2string
	la	$a0,timstr
	la	$t0,mytime
	lw	$a1,0($t0)
	jal	time2string
	nop
	# print a newline
	li	$a0,10
	li	$v0,11
	syscall
	nop
	# go back and do it all again
	j	main
	nop
# tick: update time pointed to by $a0
tick:	lw	$t0,0($a0)	# get time
	addiu	$t0,$t0,1	# increase
	andi	$t1,$t0,0xf	# check lowest digit
	sltiu	$t2,$t1,0xa	# if digit < a, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0x6	# adjust lowest digit
	andi	$t1,$t0,0xf0	# check next digit
	sltiu	$t2,$t1,0x60	# if digit < 6, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0xa0	# adjust digit
	andi	$t1,$t0,0xf00	# check minute digit
	sltiu	$t2,$t1,0xa00	# if digit < a, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0x600	# adjust digit
	andi	$t1,$t0,0xf000	# check last digit
	sltiu	$t2,$t1,0x6000	# if digit < 6, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0xa000	# adjust last digit
tiend:	sw	$t0,0($a0)	# save updated result
	jr	$ra		# return
	nop

  # you can write your code for subroutine "hexasc" below this line
  #
  
hexasc:				#  (M): Hexasc converts each digit to ascii code
	andi	$a0,$a0,0xf	# (M): check lowest digit
	add	$v0,$a0,0x30	# (M): convert to ascii
	jr	$ra

delay:				# (M): Delay function
	move 	$t0,$a0		# (M): Move a0 to t0
	li	$t1,0		# (M): Constant i set to 0
	li	$t2,1900000	# (M): constant 
	
_whileloop: 		
	ble	$t0,0,_loopexit		#  (M): while ms > 0
	sub	$t0,$t0,1		#  (M): ms = ms -1
	
_forloop:
	bge	$t1,$t2,_whileloop	# (M): if i >= 4711 _whileloo
	add	$t1,$t1,1		# (M): i = i + 1
	nop 				# (M): Null operation
	j	_forloop


_loopexit:
	jr 	$ra	
			
time2string:

	PUSH	$s0		
	PUSH	$s1
	PUSH	$ra
	
	move	$s0,$a0		# (M): Address that should be printed to s0
	move	$s1,$a1		# (M): time-info to s1
	
	move	$a0,$s1		# (M): time-info to a0
	jal 	hexasc		# (M): call hexasc
	sb 	$v0, 4($s0)	# (M): store byte
	sb	$0,5($s0)	# (M): store nullbyte, makes it so text after byte (4) isnt read
	beq	$v0, 0x39, isnine
	
ret2str:	
	srl	$a0,$s1,4	# (M): shift digits right once
	jal 	hexasc		# (M): call hexasc
	sb 	$v0,3($s0)
	
	li 	$t0,0x3A 	# (M): load 0x3A (colon)
	sb 	$t0,2($s0)	# (M): store byte
		
	srl	$a0,$s1,8	# (M): shift digits right twice
	jal 	hexasc		# (M): call hexasc
	sb 	$v0,1($s0)	# (M): store byte
	
	
	srl	$a0,$s1,12	# (M): shift digits right thrice
	jal 	hexasc		# (M): call hexasc
	sb 	$v0,0($s0)	# (M): store byte
	
	POP	$ra		
	POP	$s1
	POP	$s0
	
	jr	$ra		# (M): return
	
isnine:
	addi	$v0, $v0, 0x15
	sb	$v0, 4($s0)	# (M): store byte
	li	$v0, 0
	addi	$v0, $v0, 0x49
	sb	$v0, 5($s0)	# (M): store byte
	li	$v0, 0
	addi	$v0, $v0, 0x4E
	sb	$v0, 6($s0)	# (M): store byte
	li	$v0, 0
	addi	$v0, $v0, 0x45
	sb	$v0, 7($s0)	# (M): store byte
	sb	$0, 8($s0)	# (M): store nullbyte, makes it so text after byte (4) isnt read
	j	ret2str

	
	
	
	
